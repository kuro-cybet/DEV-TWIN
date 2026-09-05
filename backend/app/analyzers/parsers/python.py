import ast
import os
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple, Any
from .base import ParsedProject

IGNORED_DIRS = {
    ".git",
    "node_modules",
    ".venv",
    "venv",
    "env",
    "__pycache__",
    "dist",
    "build",
    ".pytest_cache",
    ".idea",
    ".vscode",
}

class PythonParser:
    """
    Parses Python source files using Python AST.
    Extracts files, modules, classes, functions, methods, imports, inheritance, and calls
    into a normalized unified representation.
    """

    def parse(self, source_path: str) -> ParsedProject:
        root = Path(source_path).resolve()
        nodes: List[Dict[str, Any]] = []
        edges: List[Dict[str, Any]] = []

        if not root.exists():
            return ParsedProject(nodes, edges, summary={"error": f"Path '{source_path}' does not exist"})

        # Discover all python files
        all_py_files: List[Path] = []
        for p in root.rglob("*.py"):
            if not any(ignored in p.parts for ignored in IGNORED_DIRS):
                all_py_files.append(p)

        if not all_py_files:
            return ParsedProject(nodes, edges, summary={"message": "No Python (.py) source files found."})

        # Build module mapping for lookup
        # file_path_str (e.g. "app/services/auth.py") -> Path
        # module_dot_path (e.g. "app.services.auth") -> file_path_str
        # stem (e.g. "auth") -> file_path_str
        file_map: Dict[str, Path] = {}
        module_to_file: Dict[str, str] = {}
        stem_to_files: Dict[str, List[str]] = {}
        defined_symbols: Dict[str, str] = {}  # "module.ClassName" or "ClassName" -> component_id

        for path in all_py_files:
            try:
                rel_path = str(path.relative_to(root)).replace("\\", "/")
            except ValueError:
                rel_path = path.name

            file_map[rel_path] = path
            
            # Module dot notation, e.g. "app/services/auth.py" -> "app.services.auth"
            parts = list(path.relative_to(root).parts)
            if parts[-1] == "__init__.py":
                mod_parts = parts[:-1]
            else:
                mod_parts = parts[:-1] + [path.stem]
            
            mod_dot = ".".join(mod_parts) if mod_parts else path.stem
            module_to_file[mod_dot] = rel_path
            
            stem = path.stem
            stem_to_files.setdefault(stem, []).append(rel_path)

        # First pass: Extract file components, classes, functions, and module hierarchy
        for rel_path, path in file_map.items():
            # File Component
            file_id = rel_path
            nodes.append({
                "id": file_id,
                "name": path.name,
                "type": "file",
                "file_path": rel_path,
                "line_number": 1,
                "language": "python",
                "parent": str(Path(rel_path).parent).replace("\\", "/") if str(Path(rel_path).parent) != "." else None,
                "metadata": {"size_bytes": path.stat().st_size if path.exists() else 0},
            })

            try:
                content = path.read_text(encoding="utf-8", errors="ignore")
                tree = ast.parse(content, filename=rel_path)
            except Exception:
                continue

            # Walk AST for Classes and Functions
            self._extract_ast_definitions(tree, file_id, rel_path, nodes, edges, defined_symbols)

        # Second pass: Extract Imports, Inheritance, and Calls
        for rel_path, path in file_map.items():
            file_id = rel_path
            try:
                content = path.read_text(encoding="utf-8", errors="ignore")
                tree = ast.parse(content, filename=rel_path)
            except Exception:
                continue

            self._extract_ast_relationships(
                tree,
                file_id,
                rel_path,
                root,
                module_to_file,
                stem_to_files,
                defined_symbols,
                edges,
            )

        # Deduplicate edges
        unique_edges = []
        seen_edges = set()
        for edge in edges:
            key = (edge["source"], edge["target"], edge["type"])
            if key not in seen_edges and edge["source"] != edge["target"]:
                seen_edges.add(key)
                unique_edges.append(edge)

        # Ensure all referenced edge targets/sources exist in nodes list
        node_ids = {n["id"] for n in nodes}
        valid_edges = [e for e in unique_edges if e["source"] in node_ids and e["target"] in node_ids]

        summary = {
            "total_files": len(all_py_files),
            "total_components": len(nodes),
            "total_relationships": len(valid_edges),
            "classes_count": sum(1 for n in nodes if n["type"] == "class"),
            "functions_count": sum(1 for n in nodes if n["type"] in ("function", "method")),
        }

        return ParsedProject(nodes=nodes, edges=valid_edges, summary=summary)

    def _extract_ast_definitions(
        self,
        tree: ast.AST,
        file_id: str,
        rel_path: str,
        nodes: List[Dict[str, Any]],
        edges: List[Dict[str, Any]],
        defined_symbols: Dict[str, str],
    ):
        for item in tree.body:
            if isinstance(item, ast.ClassDef):
                class_id = f"{file_id}::{item.name}"
                nodes.append({
                    "id": class_id,
                    "name": item.name,
                    "type": "class",
                    "file_path": rel_path,
                    "line_number": getattr(item, "lineno", 1),
                    "language": "python",
                    "parent": file_id,
                    "metadata": {
                        "bases": [ast.unparse(b) if hasattr(ast, "unparse") else getattr(b, "id", "") for b in item.bases],
                        "docstring": ast.get_docstring(item),
                    },
                })
                # Containment edge
                edges.append({
                    "id": f"{file_id}->{class_id}:contains",
                    "source": file_id,
                    "target": class_id,
                    "type": "contains",
                    "confidence": 1.0,
                    "line_number": getattr(item, "lineno", 1),
                })
                defined_symbols[item.name] = class_id
                defined_symbols[f"{rel_path}:{item.name}"] = class_id

                # Methods within class
                for class_item in item.body:
                    if isinstance(class_item, (ast.FunctionDef, ast.AsyncFunctionDef)):
                        method_id = f"{class_id}.{class_item.name}"
                        nodes.append({
                            "id": method_id,
                            "name": class_item.name,
                            "type": "method",
                            "file_path": rel_path,
                            "line_number": getattr(class_item, "lineno", 1),
                            "language": "python",
                            "parent": class_id,
                            "metadata": {
                                "is_async": isinstance(class_item, ast.AsyncFunctionDef),
                                "args": [a.arg for a in class_item.args.args],
                            },
                        })
                        edges.append({
                            "id": f"{class_id}->{method_id}:contains",
                            "source": class_id,
                            "target": method_id,
                            "type": "contains",
                            "confidence": 1.0,
                            "line_number": getattr(class_item, "lineno", 1),
                        })
                        defined_symbols[f"{item.name}.{class_item.name}"] = method_id

            elif isinstance(item, (ast.FunctionDef, ast.AsyncFunctionDef)):
                func_id = f"{file_id}::{item.name}"
                nodes.append({
                    "id": func_id,
                    "name": item.name,
                    "type": "function",
                    "file_path": rel_path,
                    "line_number": getattr(item, "lineno", 1),
                    "language": "python",
                    "parent": file_id,
                    "metadata": {
                        "is_async": isinstance(item, ast.AsyncFunctionDef),
                        "args": [a.arg for a in item.args.args],
                        "docstring": ast.get_docstring(item),
                    },
                })
                edges.append({
                    "id": f"{file_id}->{func_id}:contains",
                    "source": file_id,
                    "target": func_id,
                    "type": "contains",
                    "confidence": 1.0,
                    "line_number": getattr(item, "lineno", 1),
                })
                defined_symbols[item.name] = func_id
                defined_symbols[f"{rel_path}:{item.name}"] = func_id

    def _extract_ast_relationships(
        self,
        tree: ast.AST,
        file_id: str,
        rel_path: str,
        root: Path,
        module_to_file: Dict[str, str],
        stem_to_files: Dict[str, List[str]],
        defined_symbols: Dict[str, str],
        edges: List[Dict[str, Any]],
    ):
        file_dir_parts = list(Path(rel_path).parent.parts)

        for item in ast.walk(tree):
            # 1. Imports
            if isinstance(item, ast.Import):
                for alias in item.names:
                    target_file = self._resolve_module_import(alias.name, module_to_file, stem_to_files)
                    if target_file and target_file != file_id:
                        edges.append({
                            "id": f"{file_id}->{target_file}:import",
                            "source": file_id,
                            "target": target_file,
                            "type": "import",
                            "confidence": 1.0,
                            "line_number": getattr(item, "lineno", 1),
                            "metadata": {"imported_name": alias.name},
                        })

            elif isinstance(item, ast.ImportFrom):
                target_file = None
                # Handle relative import
                if item.level and item.level > 0:
                    # Level 1 is current dir, level 2 is parent dir, etc.
                    up_steps = item.level - 1
                    base_parts = file_dir_parts[:-up_steps] if up_steps > 0 and up_steps <= len(file_dir_parts) else file_dir_parts
                    if item.module:
                        resolved_mod = ".".join(base_parts + item.module.split("."))
                    else:
                        resolved_mod = ".".join(base_parts)
                    target_file = self._resolve_module_import(resolved_mod, module_to_file, stem_to_files)
                elif item.module:
                    target_file = self._resolve_module_import(item.module, module_to_file, stem_to_files)

                if target_file and target_file != file_id:
                    edges.append({
                        "id": f"{file_id}->{target_file}:import",
                        "source": file_id,
                        "target": target_file,
                        "type": "import",
                        "confidence": 1.0,
                        "line_number": getattr(item, "lineno", 1),
                        "metadata": {"module": item.module, "names": [n.name for n in item.names]},
                    })

                # Check if imported names are specific classes/functions
                for n in item.names:
                    if target_file:
                        symbol_key = f"{target_file}::{n.name}"
                        if symbol_key in defined_symbols:
                            edges.append({
                                "id": f"{file_id}->{symbol_key}:import",
                                "source": file_id,
                                "target": symbol_key,
                                "type": "import",
                                "confidence": 1.0,
                                "line_number": getattr(item, "lineno", 1),
                                "metadata": {"imported_symbol": n.name},
                            })

            # 2. Inheritance
            elif isinstance(item, ast.ClassDef):
                class_id = f"{file_id}::{item.name}"
                for base in item.bases:
                    base_name = getattr(base, "id", None)
                    if not base_name and isinstance(base, ast.Attribute):
                        base_name = base.attr

                    if base_name:
                        # Find base class ID in defined symbols
                        target_class_id = None
                        if base_name in defined_symbols:
                            target_class_id = defined_symbols[base_name]
                        elif f"{file_id}::{base_name}" in defined_symbols:
                            target_class_id = defined_symbols[f"{file_id}::{base_name}"]

                        if target_class_id and target_class_id != class_id:
                            edges.append({
                                "id": f"{class_id}->{target_class_id}:inheritance",
                                "source": class_id,
                                "target": target_class_id,
                                "type": "inheritance",
                                "confidence": 1.0,
                                "line_number": getattr(item, "lineno", 1),
                                "metadata": {"base_class": base_name},
                            })
                            # Also reflect inheritance at file-level for high-level graph
                            target_file = target_class_id.split("::")[0]
                            if target_file != file_id:
                                edges.append({
                                    "id": f"{file_id}->{target_file}:inheritance",
                                    "source": file_id,
                                    "target": target_file,
                                    "type": "inheritance",
                                    "confidence": 0.95,
                                    "line_number": getattr(item, "lineno", 1),
                                })

            # 3. Calls (reliable static detection)
            elif isinstance(item, ast.Call):
                func_name = None
                if isinstance(item.func, ast.Name):
                    func_name = item.func.id
                elif isinstance(item.func, ast.Attribute):
                    func_name = item.func.attr

                if func_name and func_name in defined_symbols:
                    target_func_id = defined_symbols[func_name]
                    target_file = target_func_id.split("::")[0]
                    if target_file != file_id:
                        edges.append({
                            "id": f"{file_id}->{target_file}:call",
                            "source": file_id,
                            "target": target_file,
                            "type": "call",
                            "confidence": 0.8,
                            "line_number": getattr(item, "lineno", 1),
                            "metadata": {"called_function": func_name},
                        })

    def _resolve_module_import(
        self,
        module_path: str,
        module_to_file: Dict[str, str],
        stem_to_files: Dict[str, List[str]],
    ) -> Optional[str]:
        # Exact match
        if module_path in module_to_file:
            return module_to_file[module_path]

        # Try prefix slices: e.g. "app.services.auth.AuthService" -> "app.services.auth"
        parts = module_path.split(".")
        for i in range(len(parts) - 1, 0, -1):
            sub_mod = ".".join(parts[:i])
            if sub_mod in module_to_file:
                return module_to_file[sub_mod]

        # Try stem match if single module or terminal part
        terminal = parts[-1]
        if terminal in stem_to_files and len(stem_to_files[terminal]) == 1:
            return stem_to_files[terminal][0]

        first_part = parts[0]
        if first_part in stem_to_files and len(stem_to_files[first_part]) == 1:
            return stem_to_files[first_part][0]

        return None

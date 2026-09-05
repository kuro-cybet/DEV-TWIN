from typing import Dict, List, Any, Optional
from pathlib import Path

class ArchitectureBuilder:
    """
    Constructs multi-level architecture representations from the unified graph.
    Supports file-level aggregation and component-level views.
    """

    def build_file_level_graph(self, raw_nodes: List[Dict[str, Any]], raw_edges: List[Dict[str, Any]]) -> Dict[str, Any]:
        """
        Aggregates classes and functions into their parent files
        to produce a clean, uncluttered architectural overview graph.
        """
        # Collect only file nodes
        file_nodes = [n for n in raw_nodes if n.get("type") == "file"]
        file_ids = {n["id"] for n in file_nodes}

        # Map non-file node IDs to their parent file ID
        node_to_file = {}
        for n in raw_nodes:
            if n.get("type") == "file":
                node_to_file[n["id"]] = n["id"]
            else:
                file_path = n.get("file_path") or (n.get("parent") if n.get("parent") in file_ids else None)
                if file_path and file_path in file_ids:
                    node_to_file[n["id"]] = file_path

        # Aggregate edges between files
        file_edges = []
        seen_file_edges = set()

        for edge in raw_edges:
            src = node_to_file.get(edge["source"], edge["source"])
            tgt = node_to_file.get(edge["target"], edge["target"])

            if src in file_ids and tgt in file_ids and src != tgt:
                edge_key = (src, tgt, edge.get("type", "dependency"))
                if edge_key not in seen_file_edges:
                    seen_file_edges.add(edge_key)
                    file_edges.append({
                        "id": f"{src}->{tgt}:{edge.get('type', 'dependency')}",
                        "source": src,
                        "target": tgt,
                        "type": edge.get("type", "import"),
                        "confidence": edge.get("confidence", 1.0),
                    })

        return {
            "nodes": file_nodes,
            "edges": file_edges,
            "total_nodes": len(file_nodes),
            "total_edges": len(file_edges),
            "modules_count": len(set(n.get("parent") for n in file_nodes if n.get("parent"))),
            "classes_count": sum(1 for n in raw_nodes if n.get("type") == "class"),
            "functions_count": sum(1 for n in raw_nodes if n.get("type") in ("function", "method")),
        }

    def build_component_inventory(self, raw_nodes: List[Dict[str, Any]]) -> Dict[str, Any]:
        by_type = {}
        for n in raw_nodes:
            t = n.get("type", "file")
            by_type[t] = by_type.get(t, 0) + 1

        return {
            "components": raw_nodes,
            "total": len(raw_nodes),
            "by_type": by_type,
        }

architecture_builder = ArchitectureBuilder()

from typing import Dict, List, Any
from datetime import datetime

class ReportGenerator:
    """
    Generates health summaries, architectural inventory, and dependency diagnostic reports.
    """

    def generate_health_summary(
        self,
        project_id: str,
        project_name: str,
        metrics: Dict[str, Any],
        cycles: Dict[str, Any],
    ) -> Dict[str, Any]:
        total_components = metrics.get("components", 0)
        cycle_count = cycles.get("count", 0)
        density = metrics.get("density", 0.0)
        high_coupling_count = len(metrics.get("high_coupling", []))

        # Calculate health score (0 - 100)
        score = 100
        # Penalize circular dependencies heavily
        score -= min(cycle_count * 15, 45)

        # Penalize high coupling concentration
        if total_components > 0:
            coupling_penalty = int((high_coupling_count / total_components) * 25)
            score -= min(coupling_penalty, 25)

        # Penalize excessive graph density
        if density > 0.4:
            score -= 20
        elif density > 0.2:
            score -= 10

        score = max(0, min(100, score))

        if score >= 90:
            grade = "A"
        elif score >= 75:
            grade = "B"
        elif score >= 60:
            grade = "C"
        elif score >= 40:
            grade = "D"
        else:
            grade = "F"

        # Generate top risks
        top_risks = []
        recommendations = []

        if cycle_count > 0:
            top_risks.append({
                "severity": "HIGH",
                "title": f"{cycle_count} Circular Dependency Cycle(s) Detected",
                "description": f"Cyclic dependencies between {len(cycles.get('involved_components', []))} components create mutual coupling, hindering independent unit testing and modular deployment.",
            })
            recommendations.append("Break circular dependency loops using Dependency Inversion (interfaces/abstract classes) or event-driven patterns.")

        if high_coupling_count > 0:
            top_risks.append({
                "severity": "MEDIUM",
                "title": f"{high_coupling_count} High-Coupling Components Identified",
                "description": "Components with excessive incoming/outgoing connections have high change blast radiuses.",
            })
            recommendations.append("Refactor God-classes and large orchestrators by splitting responsibilities across dedicated service layers.")

        if density > 0.25:
            top_risks.append({
                "severity": "MEDIUM",
                "title": f"Elevated Graph Density ({round(density * 100, 1)}%)",
                "description": "High interconnectedness between system components indicates potential boundary erosion.",
            })
            recommendations.append("Establish strict layer boundaries (e.g. Presentation -> Service -> Domain -> Data) to restrict cross-layer calls.")

        if not top_risks:
            top_risks.append({
                "severity": "LOW",
                "title": "Healthy Architecture Baseline",
                "description": "No cyclic dependencies or high-risk coupling anomalies detected in the current code structure.",
            })
            recommendations.append("Maintain test coverage and monitor coupling as new features are added.")

        summary_text = (
            f"Project '{project_name}' scored {score}/100 (Grade {grade}). "
            f"Analyzed {total_components} components and {metrics.get('dependencies', 0)} dependencies "
            f"with {cycle_count} circular dependency cycles detected."
        )

        return {
            "project_id": project_id,
            "project_name": project_name,
            "health_score": score,
            "health_grade": grade,
            "summary": summary_text,
            "metrics": metrics,
            "circular_dependencies": cycles,
            "critical_components": metrics.get("critical_components", []),
            "top_risks": top_risks,
            "recommendations": recommendations,
            "generated_at": datetime.utcnow().isoformat(),
        }

    def generate_architecture_report(
        self,
        project_id: str,
        project_name: str,
        graph_data: Dict[str, Any],
    ) -> Dict[str, Any]:
        nodes = graph_data.get("nodes", [])
        edges = graph_data.get("edges", [])

        files = [n for n in nodes if n.get("type") == "file"]
        classes = [n for n in nodes if n.get("type") == "class"]
        functions = [n for n in nodes if n.get("type") in ("function", "method")]

        # Group by folder / module
        modules_map: Dict[str, List[Dict[str, Any]]] = {}
        for f in files:
            parent = f.get("parent") or "root"
            modules_map.setdefault(parent, []).append(f)

        modules_breakdown = [
            {"module": mod, "files_count": len(f_list), "files": [f["name"] for f in f_list]}
            for mod, f_list in modules_map.items()
        ]

        # Inheritance relationships
        inheritance_edges = [e for e in edges if e.get("type") == "inheritance"]
        inheritance_hierarchies = [
            {"derived": e["source"], "base": e["target"], "confidence": e.get("confidence", 1.0)}
            for e in inheritance_edges
        ]

        by_type = {
            "file": len(files),
            "class": len(classes),
            "function": len(functions),
        }

        return {
            "project_id": project_id,
            "project_name": project_name,
            "total_files": len(files),
            "total_modules": len(modules_map),
            "total_classes": len(classes),
            "total_functions": len(functions),
            "total_components": len(nodes),
            "modules_breakdown": modules_breakdown,
            "inheritance_hierarchies": inheritance_hierarchies,
            "components_by_type": by_type,
            "generated_at": datetime.utcnow().isoformat(),
        }

    def generate_dependency_report(
        self,
        project_id: str,
        project_name: str,
        metrics: Dict[str, Any],
        cycles: Dict[str, Any],
    ) -> Dict[str, Any]:
        high_coupling = metrics.get("high_coupling", [])
        avg_instability = (
            round(sum(c.get("instability", 0.0) for c in high_coupling) / len(high_coupling), 2)
            if high_coupling
            else 0.0
        )

        return {
            "project_id": project_id,
            "project_name": project_name,
            "total_dependencies": metrics.get("dependencies", 0),
            "graph_density": metrics.get("density", 0.0),
            "cycles_count": cycles.get("count", 0),
            "cycles": cycles.get("cycles", []),
            "coupling_analysis": high_coupling,
            "critical_components": metrics.get("critical_components", []),
            "instability_index_avg": avg_instability,
            "generated_at": datetime.utcnow().isoformat(),
        }

report_generator = ReportGenerator()

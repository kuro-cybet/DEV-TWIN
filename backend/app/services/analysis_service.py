from typing import Dict, Any, Optional
from app.repositories.project_repository import project_repository
from app.analyzers.architecture.builder import architecture_builder
from app.analyzers.dependencies.analyzer import DependencyAnalyzer
from app.analyzers.impact.analyzer import ImpactAnalyzer
from app.analyzers.reports.generator import report_generator

class AnalysisService:
    """
    Unified analysis service.
    Consumes the single normalized project graph to serve:
    - Module 1: Architecture graph, component inventory, module breakdown
    - Module 2: Dependency graph, coupling metrics, cycle detection, potential change impact, reports
    """

    def _get_project_and_graph(self, project_id: str):
        project = project_repository.get(project_id)
        if not project:
            raise KeyError(f"Project '{project_id}' not found")
        if project.status != "analyzed" or not project.graph or not project.graph.get("nodes"):
            raise ValueError(f"Project '{project_id}' has not been analyzed yet. Please run analysis first.")
        return project, project.graph

    def graph(self, project_id: str, level: str = "file") -> Dict[str, Any]:
        project, raw_graph = self._get_project_and_graph(project_id)
        if level == "file":
            return architecture_builder.build_file_level_graph(raw_graph.get("nodes", []), raw_graph.get("edges", []))
        return {
            "nodes": raw_graph.get("nodes", []),
            "edges": raw_graph.get("edges", []),
            "total_nodes": len(raw_graph.get("nodes", [])),
            "total_edges": len(raw_graph.get("edges", [])),
            "classes_count": sum(1 for n in raw_graph.get("nodes", []) if n.get("type") == "class"),
            "functions_count": sum(1 for n in raw_graph.get("nodes", []) if n.get("type") in ("function", "method")),
        }

    def components(self, project_id: str) -> Dict[str, Any]:
        _, raw_graph = self._get_project_and_graph(project_id)
        return architecture_builder.build_component_inventory(raw_graph.get("nodes", []))

    def dependency_graph(self, project_id: str) -> Dict[str, Any]:
        project, raw_graph = self._get_project_and_graph(project_id)
        analyzer = DependencyAnalyzer(raw_graph)
        cycles_data = analyzer.cycles()
        metrics_data = analyzer.metrics()
        
        # Build clean dependency graph representation
        file_graph = architecture_builder.build_file_level_graph(raw_graph.get("nodes", []), raw_graph.get("edges", []))
        return {
            "nodes": file_graph["nodes"],
            "edges": file_graph["edges"],
            "cycles": cycles_data["cycles"],
            "metrics": metrics_data,
        }

    def metrics(self, project_id: str) -> Dict[str, Any]:
        _, raw_graph = self._get_project_and_graph(project_id)
        analyzer = DependencyAnalyzer(raw_graph)
        return analyzer.metrics()

    def cycles(self, project_id: str) -> Dict[str, Any]:
        _, raw_graph = self._get_project_and_graph(project_id)
        analyzer = DependencyAnalyzer(raw_graph)
        return analyzer.cycles()

    def impact(self, project_id: str, component_id: str) -> Dict[str, Any]:
        _, raw_graph = self._get_project_and_graph(project_id)
        analyzer = ImpactAnalyzer(raw_graph)
        return analyzer.analyze(component_id)

    def summary_report(self, project_id: str) -> Dict[str, Any]:
        project, raw_graph = self._get_project_and_graph(project_id)
        analyzer = DependencyAnalyzer(raw_graph)
        metrics_data = analyzer.metrics()
        cycles_data = analyzer.cycles()
        return report_generator.generate_health_summary(project.id, project.name, metrics_data, cycles_data)

    def architecture_report(self, project_id: str) -> Dict[str, Any]:
        project, raw_graph = self._get_project_and_graph(project_id)
        return report_generator.generate_architecture_report(project.id, project.name, raw_graph)

    def dependency_report(self, project_id: str) -> Dict[str, Any]:
        project, raw_graph = self._get_project_and_graph(project_id)
        analyzer = DependencyAnalyzer(raw_graph)
        metrics_data = analyzer.metrics()
        cycles_data = analyzer.cycles()
        return report_generator.generate_dependency_report(project.id, project.name, metrics_data, cycles_data)

analysis_service = AnalysisService()

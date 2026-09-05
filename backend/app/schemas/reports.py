from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional

class HealthSummaryReport(BaseModel):
    project_id: str
    project_name: str
    health_score: int  # 0 to 100
    health_grade: str  # A, B, C, D, F
    summary: str
    metrics: Dict[str, Any]
    circular_dependencies: Dict[str, Any]
    critical_components: List[Dict[str, Any]]
    top_risks: List[Dict[str, Any]]
    recommendations: List[str]
    generated_at: str

class ArchitectureReport(BaseModel):
    project_id: str
    project_name: str
    total_files: int
    total_modules: int
    total_classes: int
    total_functions: int
    total_components: int
    modules_breakdown: List[Dict[str, Any]]
    inheritance_hierarchies: List[Dict[str, Any]]
    components_by_type: Dict[str, int]
    generated_at: str

class DependencyReport(BaseModel):
    project_id: str
    project_name: str
    total_dependencies: int
    graph_density: float
    cycles_count: int
    cycles: List[List[str]]
    coupling_analysis: List[Dict[str, Any]]
    critical_components: List[Dict[str, Any]]
    instability_index_avg: float
    generated_at: str

from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional

class CouplingItem(BaseModel):
    component: str
    name: str
    type: str = "file"
    incoming: int
    outgoing: int
    total: int
    instability: float = 0.0
    coupling_level: str  # "LOW" | "MEDIUM" | "HIGH"
    explanation: str

class CriticalComponentItem(BaseModel):
    component: str
    name: str
    type: str = "file"
    score: float
    level: str  # "HIGH" | "MEDIUM" | "LOW"
    incoming: int
    outgoing: int
    betweenness: float
    explanation: str

class DependencyMetricsResponse(BaseModel):
    components: int
    dependencies: int
    density: float
    density_percentage: float
    avg_dependencies_per_component: float
    high_coupling: List[CouplingItem]
    critical_components: List[CriticalComponentItem]
    coupling_distribution: Dict[str, int] = Field(default_factory=dict)

class CyclesResponse(BaseModel):
    count: int
    cycles: List[List[str]]
    involved_components: List[str]
    severity: str  # "NONE" | "LOW" | "MEDIUM" | "HIGH" | "CRITICAL"

class DependencyGraphResponse(BaseModel):
    nodes: List[Dict[str, Any]]
    edges: List[Dict[str, Any]]
    cycles: List[List[str]]
    metrics: Optional[Dict[str, Any]] = None

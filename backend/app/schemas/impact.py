from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional

class ImpactRequest(BaseModel):
    component_id: str = Field(description="The component ID to analyze impact for")

class ImpactNode(BaseModel):
    id: str
    name: str
    type: str = "file"
    file_path: str
    depth: int
    impact_type: str  # "direct" | "indirect"

class ImpactResponse(BaseModel):
    selected_component: str
    selected_component_name: str
    total_affected: int
    direct_count: int
    indirect_count: int
    direct_impact: List[str]
    indirect_impact: List[str]
    direct_impact_nodes: List[ImpactNode] = Field(default_factory=list)
    indirect_impact_nodes: List[ImpactNode] = Field(default_factory=list)
    affected_components: List[str]
    impact_graph: Dict[str, Any] = Field(default_factory=lambda: {"nodes": [], "edges": []})
    disclaimer: str = "POTENTIAL CHANGE IMPACT: This calculation represents static dependency reachability. Actual runtime impact may vary based on execution flow and conditional branches."

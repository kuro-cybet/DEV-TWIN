from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any

class ComponentSchema(BaseModel):
    id: str
    name: str
    type: str  # file, module, class, function, method
    file_path: str
    line_number: Optional[int] = None
    language: str = "python"
    parent: Optional[str] = None
    metadata: Dict[str, Any] = Field(default_factory=dict)

class RelationshipSchema(BaseModel):
    id: str
    source: str
    target: str
    type: str  # import, inheritance, call, contains
    confidence: float = 1.0
    line_number: Optional[int] = None
    metadata: Dict[str, Any] = Field(default_factory=dict)

class ArchitectureGraphResponse(BaseModel):
    nodes: List[ComponentSchema]
    edges: List[RelationshipSchema]
    total_nodes: int = 0
    total_edges: int = 0
    modules_count: int = 0
    classes_count: int = 0
    functions_count: int = 0

class ArchitectureComponentsResponse(BaseModel):
    components: List[ComponentSchema]
    total: int
    by_type: Dict[str, int] = Field(default_factory=dict)

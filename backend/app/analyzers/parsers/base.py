from dataclasses import dataclass, field
from typing import List, Dict, Any

@dataclass
class ParsedProject:
    nodes: List[Dict[str, Any]] = field(default_factory=list)
    edges: List[Dict[str, Any]] = field(default_factory=list)
    summary: Dict[str, Any] = field(default_factory=dict)

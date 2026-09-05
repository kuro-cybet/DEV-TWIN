from dataclasses import dataclass, field
from typing import Optional, Dict, Any, List
from datetime import datetime

@dataclass
class Project:
    id: str
    name: str
    repository_url: Optional[str] = None
    source_path: Optional[str] = None
    status: str = "created"  # "created", "uploaded", "analyzed", "failed", "awaiting_source"
    graph: Dict[str, List[Dict[str, Any]]] = field(default_factory=lambda: {"nodes": [], "edges": []})
    created_at: str = field(default_factory=lambda: datetime.utcnow().isoformat())
    updated_at: str = field(default_factory=lambda: datetime.utcnow().isoformat())

    def to_dict(self) -> Dict[str, Any]:
        return {
            "id": self.id,
            "name": self.name,
            "repository_url": self.repository_url,
            "source_path": self.source_path,
            "status": self.status,
            "graph": self.graph,
            "created_at": self.created_at,
            "updated_at": self.updated_at,
        }

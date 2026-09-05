from dataclasses import dataclass, field
from typing import Optional, Dict, Any

@dataclass
class Component:
    id: str
    name: str
    type: str  # "file" | "module" | "class" | "function" | "method"
    file_path: str
    line_number: Optional[int] = None
    language: str = "python"
    parent: Optional[str] = None
    metadata: Dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> Dict[str, Any]:
        return {
            "id": self.id,
            "name": self.name,
            "type": self.type,
            "file_path": self.file_path,
            "line_number": self.line_number,
            "language": self.language,
            "parent": self.parent,
            "metadata": self.metadata,
        }

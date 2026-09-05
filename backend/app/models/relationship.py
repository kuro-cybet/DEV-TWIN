from dataclasses import dataclass, field
from typing import Optional, Dict, Any

@dataclass
class Relationship:
    id: str
    source: str
    target: str
    type: str  # "import" | "inheritance" | "call" | "contains"
    confidence: float = 1.0
    line_number: Optional[int] = None
    metadata: Dict[str, Any] = field(default_factory=dict)

    def to_dict(self) -> Dict[str, Any]:
        return {
            "id": self.id,
            "source": self.source,
            "target": self.target,
            "type": self.type,
            "confidence": self.confidence,
            "line_number": self.line_number,
            "metadata": self.metadata,
        }

import os
from dataclasses import dataclass, field
from pathlib import Path

@dataclass(frozen=True)
class Settings:
    cors_origins: list[str] = field(
        default_factory=lambda: [
            x.strip()
            for x in os.getenv(
                "CORS_ORIGINS",
                "http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173,http://127.0.0.1:3000",
            ).split(",")
            if x.strip()
        ]
    )
    database_url: str = os.getenv("DATABASE_URL", "sqlite:///./devtwin.db")
    host: str = os.getenv("HOST", "0.0.0.0")
    port: int = int(os.getenv("PORT", "8000"))
    max_upload_size_mb: int = int(os.getenv("MAX_UPLOAD_SIZE_MB", "50"))
    storage_dir: Path = Path(os.getenv("STORAGE_DIR", "./data/projects"))

settings = Settings()

import uuid
from datetime import datetime
from pathlib import Path
from typing import Optional, List, Dict, Any
from app.models.project import Project
from app.repositories.project_repository import project_repository
from app.schemas.project import ProjectCreate
from app.analyzers.parsers.python import PythonParser
from app.services.ingestion_service import ingestion_service

class ProjectService:
    def _validate_source(self, source_path: str, context: str = "source") -> None:
        """Verify extracted source exists and contains Python files."""
        src = Path(source_path)
        if not src.exists():
            raise ValueError(f"Ingestion failed: extracted {context} path does not exist on disk.")
        if not any(src.rglob("*.py")):
            raise ValueError(
                f"No Python (.py) source files found in the ingested {context}. "
                "DevTwin currently supports Python project analysis only."
            )

    def create(self, payload: ProjectCreate) -> Project:
        project_id = str(uuid.uuid4())
        source_path = payload.source_path

        # If a GitHub repository URL is provided, safely ingest it
        status = "created"
        if payload.repository_url:
            source_path = ingestion_service.ingest_github_repo(project_id, payload.repository_url)
            status = "uploaded"

        # Validate ingested source contains analyzable Python files
        if source_path:
            self._validate_source(source_path, context="repository")

        project = Project(
            id=project_id,
            name=payload.name,
            repository_url=payload.repository_url,
            source_path=source_path,
            status=status,
            graph={"nodes": [], "edges": []},
            created_at=datetime.utcnow().isoformat(),
            updated_at=datetime.utcnow().isoformat(),
        )
        return project_repository.save(project)

    def create_from_zip(self, name: str, zip_bytes: bytes, filename: str) -> Project:
        project_id = str(uuid.uuid4())
        source_path = ingestion_service.ingest_zip_bytes(project_id, zip_bytes)

        # Validate extracted source contains analyzable Python files
        self._validate_source(source_path, context=f"archive '{filename}'")

        project = Project(
            id=project_id,
            name=name or filename.replace(".zip", ""),
            repository_url=None,
            source_path=source_path,
            status="uploaded",
            graph={"nodes": [], "edges": []},
            created_at=datetime.utcnow().isoformat(),
            updated_at=datetime.utcnow().isoformat(),
        )
        return project_repository.save(project)

    def get(self, project_id: str) -> Optional[Project]:
        return project_repository.get(project_id)

    def list(self) -> List[Project]:
        return project_repository.list()

    def delete(self, project_id: str) -> bool:
        project = project_repository.get(project_id)
        if not project:
            return False
        return project_repository.delete(project_id)

    def analyze(self, project_id: str) -> Dict[str, Any]:
        project = project_repository.get(project_id)
        if not project:
            raise KeyError(f"Project '{project_id}' not found")

        # If repo URL given but not yet ingested, ingest it now
        if not project.source_path and project.repository_url:
            try:
                project.source_path = ingestion_service.ingest_github_repo(project.id, project.repository_url)
            except Exception as e:
                project.status = "failed"
                project_repository.save(project)
                raise ValueError(f"Failed to ingest GitHub repository: {e}")

        if not project.source_path:
            project.status = "awaiting_source"
            project_repository.save(project)
            return {
                "project_id": project_id,
                "status": project.status,
                "message": "No source path or repository archive available. Please upload source code or specify a GitHub repository.",
                "components": 0,
                "dependencies": 0,
                "timestamp": datetime.utcnow().isoformat(),
            }

        # Parse Once -> Normalize Once -> Store Once -> Reuse Everywhere
        parsed = PythonParser().parse(project.source_path)
        project.graph = {"nodes": parsed.nodes, "edges": parsed.edges}
        project.status = "analyzed"
        project.updated_at = datetime.utcnow().isoformat()
        project_repository.save(project)

        return {
            "project_id": project_id,
            "status": project.status,
            "message": "Project analyzed successfully.",
            "components": len(parsed.nodes),
            "dependencies": len(parsed.edges),
            "timestamp": datetime.utcnow().isoformat(),
        }

project_service = ProjectService()

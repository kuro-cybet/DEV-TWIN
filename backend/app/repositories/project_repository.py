import json
from typing import Optional, List
from datetime import datetime
from app.models.project import Project
from app.core.database import db

class ProjectRepository:
    def save(self, project: Project) -> Project:
        project.updated_at = datetime.utcnow().isoformat()
        graph_str = json.dumps(project.graph)
        
        # Check if project exists
        existing = db.fetchone("SELECT id FROM projects WHERE id = ?", (project.id,))
        if existing:
            db.execute(
                """
                UPDATE projects 
                SET name = ?, repository_url = ?, source_path = ?, status = ?, graph_data = ?, updated_at = ?
                WHERE id = ?
                """,
                (
                    project.name,
                    project.repository_url,
                    project.source_path,
                    project.status,
                    graph_str,
                    project.updated_at,
                    project.id,
                )
            )
        else:
            db.execute(
                """
                INSERT INTO projects (id, name, repository_url, source_path, status, graph_data, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    project.id,
                    project.name,
                    project.repository_url,
                    project.source_path,
                    project.status,
                    graph_str,
                    project.created_at,
                    project.updated_at,
                )
            )
        return project

    def get(self, project_id: str) -> Optional[Project]:
        row = db.fetchone("SELECT * FROM projects WHERE id = ?", (project_id,))
        if not row:
            return None
        return self._map_row(row)

    def list(self) -> List[Project]:
        rows = db.fetchall("SELECT * FROM projects ORDER BY created_at DESC")
        return [self._map_row(row) for row in rows]

    def delete(self, project_id: str) -> bool:
        db.execute("DELETE FROM analyses WHERE project_id = ?", (project_id,))
        res = db.execute("DELETE FROM projects WHERE id = ?", (project_id,))
        return True

    def _map_row(self, row: dict) -> Project:
        graph_raw = row.get("graph_data")
        graph = {"nodes": [], "edges": []}
        if graph_raw:
            if isinstance(graph_raw, str):
                try:
                    graph = json.loads(graph_raw)
                except Exception:
                    graph = {"nodes": [], "edges": []}
            elif isinstance(graph_raw, dict):
                graph = graph_raw

        return Project(
            id=str(row["id"]),
            name=str(row["name"]),
            repository_url=row.get("repository_url"),
            source_path=row.get("source_path"),
            status=str(row.get("status", "created")),
            graph=graph,
            created_at=str(row.get("created_at") or datetime.utcnow().isoformat()),
            updated_at=str(row.get("updated_at") or datetime.utcnow().isoformat()),
        )

project_repository = ProjectRepository()

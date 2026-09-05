from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any

class ProjectCreate(BaseModel):
    name: str = Field(min_length=1, max_length=120, description="Project display name")
    repository_url: Optional[str] = Field(default=None, description="GitHub repository URL")
    source_path: Optional[str] = Field(default=None, description="Local path or temporary extraction path")

class ProjectResponse(BaseModel):
    id: str
    name: str
    repository_url: Optional[str] = None
    source_path: Optional[str] = None
    status: str = "created"
    created_at: Optional[str] = None
    updated_at: Optional[str] = None
    components_count: Optional[int] = 0
    dependencies_count: Optional[int] = 0

class ProjectAnalyzeResponse(BaseModel):
    project_id: str
    status: str
    message: Optional[str] = None
    components: int = 0
    dependencies: int = 0
    timestamp: Optional[str] = None

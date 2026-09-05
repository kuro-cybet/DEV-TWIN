from typing import List, Optional
from fastapi import APIRouter, HTTPException, UploadFile, File, Form, status
from app.schemas.project import ProjectCreate, ProjectResponse, ProjectAnalyzeResponse
from app.services.project_service import project_service

router = APIRouter()

@router.post("", response_model=ProjectResponse, status_code=status.HTTP_201_CREATED)
def create_project(payload: ProjectCreate):
    try:
        project = project_service.create(payload)
        return project.to_dict()
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to create project: {str(e)}")

@router.post("/upload", response_model=ProjectResponse, status_code=status.HTTP_201_CREATED)
async def upload_project_zip(
    name: Optional[str] = Form(None),
    file: UploadFile = File(...),
):
    if not file.filename.endswith(".zip"):
        raise HTTPException(status_code=400, detail="Only .zip project archives are supported.")
    
    try:
        zip_bytes = await file.read()
        project_name = name or file.filename.rsplit(".", 1)[0]
        project = project_service.create_from_zip(project_name, zip_bytes, file.filename)
        return project.to_dict()
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to upload and extract project: {str(e)}")

@router.get("", response_model=List[ProjectResponse])
def list_projects():
    projects = project_service.list()
    results = []
    for p in projects:
        p_dict = p.to_dict()
        nodes = p.graph.get("nodes", []) if p.graph else []
        edges = p.graph.get("edges", []) if p.graph else []
        p_dict["components_count"] = len(nodes)
        p_dict["dependencies_count"] = len(edges)
        results.append(p_dict)
    return results

@router.get("/{project_id}", response_model=ProjectResponse)
def get_project(project_id: str):
    project = project_service.get(project_id)
    if not project:
        raise HTTPException(status_code=404, detail=f"Project '{project_id}' not found.")
    p_dict = project.to_dict()
    nodes = project.graph.get("nodes", []) if project.graph else []
    edges = project.graph.get("edges", []) if project.graph else []
    p_dict["components_count"] = len(nodes)
    p_dict["dependencies_count"] = len(edges)
    return p_dict

@router.delete("/{project_id}", status_code=status.HTTP_200_OK)
def delete_project(project_id: str):
    success = project_service.delete(project_id)
    if not success:
        raise HTTPException(status_code=404, detail=f"Project '{project_id}' not found.")
    return {"message": f"Project '{project_id}' deleted successfully.", "id": project_id}

@router.post("/{project_id}/analyze", response_model=ProjectAnalyzeResponse)
def analyze_project(project_id: str):
    try:
        result = project_service.analyze(project_id)
        return result
    except KeyError:
        raise HTTPException(status_code=404, detail=f"Project '{project_id}' not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis failed: {str(e)}")

from fastapi import APIRouter, HTTPException, Query
from app.services.analysis_service import analysis_service

router = APIRouter()

@router.get("/{project_id}/architecture")
def get_architecture_overview(project_id: str):
    try:
        graph = analysis_service.graph(project_id, level="file")
        components = analysis_service.components(project_id)
        return {
            "project_id": project_id,
            "file_graph": graph,
            "inventory": components,
        }
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{project_id}/architecture/components")
def get_architecture_components(project_id: str):
    try:
        return analysis_service.components(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{project_id}/architecture/graph")
def get_architecture_graph(
    project_id: str,
    level: str = Query("file", description="Level of detail: 'file' or 'component'")
):
    try:
        return analysis_service.graph(project_id, level=level)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

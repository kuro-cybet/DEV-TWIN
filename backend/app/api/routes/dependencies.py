from fastapi import APIRouter, HTTPException
from app.services.analysis_service import analysis_service

router = APIRouter()

@router.get("/{project_id}/dependencies")
def get_dependencies_overview(project_id: str):
    try:
        metrics = analysis_service.metrics(project_id)
        cycles = analysis_service.cycles(project_id)
        return {
            "project_id": project_id,
            "metrics": metrics,
            "cycles": cycles,
        }
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{project_id}/dependencies/graph")
def get_dependencies_graph(project_id: str):
    try:
        return analysis_service.dependency_graph(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{project_id}/dependencies/metrics")
def get_dependencies_metrics(project_id: str):
    try:
        return analysis_service.metrics(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

@router.get("/{project_id}/dependencies/cycles")
def get_dependencies_cycles(project_id: str):
    try:
        return analysis_service.cycles(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))

from fastapi import APIRouter, HTTPException
from app.schemas.impact import ImpactRequest, ImpactResponse
from app.services.analysis_service import analysis_service

router = APIRouter()

@router.get("/{project_id}/impact/{component_id:path}", response_model=ImpactResponse)
def get_change_impact(project_id: str, component_id: str):
    try:
        return analysis_service.impact(project_id, component_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Impact analysis failed: {str(e)}")

@router.post("/{project_id}/impact/analyze", response_model=ImpactResponse)
def analyze_change_impact(project_id: str, payload: ImpactRequest):
    try:
        return analysis_service.impact(project_id, payload.component_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Impact analysis failed: {str(e)}")

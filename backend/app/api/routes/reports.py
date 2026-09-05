from fastapi import APIRouter, HTTPException
from app.schemas.reports import HealthSummaryReport, ArchitectureReport, DependencyReport
from app.services.analysis_service import analysis_service

router = APIRouter()

@router.get("/{project_id}/reports/summary", response_model=HealthSummaryReport)
def get_summary_report(project_id: str):
    try:
        return analysis_service.summary_report(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate summary report: {str(e)}")

@router.get("/{project_id}/reports/architecture", response_model=ArchitectureReport)
def get_architecture_report(project_id: str):
    try:
        return analysis_service.architecture_report(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate architecture report: {str(e)}")

@router.get("/{project_id}/reports/dependencies", response_model=DependencyReport)
def get_dependency_report(project_id: str):
    try:
        return analysis_service.dependency_report(project_id)
    except KeyError:
        raise HTTPException(status_code=404, detail="Project not found.")
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to generate dependency report: {str(e)}")

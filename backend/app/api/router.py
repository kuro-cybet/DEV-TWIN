from fastapi import APIRouter
from app.api.routes import health, projects, architecture, dependencies, impact, reports

api_router = APIRouter()
api_router.include_router(health.router, tags=["health"])
api_router.include_router(projects.router, prefix="/projects", tags=["projects"])
api_router.include_router(architecture.router, prefix="/projects", tags=["architecture"])
api_router.include_router(dependencies.router, prefix="/projects", tags=["dependencies"])
api_router.include_router(impact.router, prefix="/projects", tags=["impact"])
api_router.include_router(reports.router, prefix="/projects", tags=["reports"])

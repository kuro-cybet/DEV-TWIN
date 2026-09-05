from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.router import api_router
from app.core.config import settings

app = FastAPI(
    title="DevTwin API",
    description="Intelligent Software Architecture Visualization & Dependency Analysis Platform",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"] if "*" in settings.cors_origins else settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(api_router, prefix="/api/v1")

@app.get("/")
def root():
    return {
        "name": "DevTwin API",
        "version": "1.0.0",
        "description": "Software Architecture Visualization & Dependency Analysis Platform",
        "docs_url": "/docs",
        "health_url": "/api/v1/health",
    }

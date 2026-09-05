from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
def health_check():
    return {
        "status": "ok",
        "service": "DevTwin Architecture & Dependency Intelligence API",
        "version": "1.0.0",
    }

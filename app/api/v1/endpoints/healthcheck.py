from core import settings
from fastapi import APIRouter

router = APIRouter(prefix=settings.api_prefix.healthcheck, tags=["Healthcheck"])


@router.get("/healthcheck")
async def healthcheck():
    return {
        "message": "success",
    }

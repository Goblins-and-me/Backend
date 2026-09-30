from core import settings
from fastapi import APIRouter

from .endpoints.auth import router as auth_router
from .endpoints.healthcheck import router as healthcheck_router
from .endpoints.user import router as user_router

router = APIRouter(prefix=settings.api_prefix.v1_prefix, tags=["V1"])

router.include_router(
    auth_router,
)

router.include_router(
    healthcheck_router,
)


router.include_router(user_router)

from typing import Annotated

from api.dependencies import dep_get_auth_service, get_access_token
from core import settings
from domain.services import AuthService
from fastapi import APIRouter, Depends, Request
from schemas import UserSchemaRead

router = APIRouter(
    tags=["User"],
    prefix=settings.api_prefix.user,
)


@router.get("/user/me", response_model=UserSchemaRead)
async def get_user(
    access_token: Annotated[str, Depends(get_access_token)],
    service: Annotated[AuthService, Depends(dep_get_auth_service)],
):
    return await service.get_current_user(access_token)

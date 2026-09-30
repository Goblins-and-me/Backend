import time
import uuid
from typing import Annotated

from api.dependencies import dep_get_auth_service, get_refresh_token
from api.utils import delete_cookies, set_cookies
from core import limiter, settings
from domain.services import AuthService
from fastapi import APIRouter, BackgroundTasks, Depends, Request, Response
from schemas import LoginSchema, UserSchemaAdd, UserSchemaRead, VerifyEmailSchema

router = APIRouter(
    prefix=settings.api_prefix.auth,
    tags=["Auth"],
)


@router.post("/register")
async def register_user(
    background_tasks: BackgroundTasks,
    user_data: UserSchemaAdd,
    service: Annotated[AuthService, Depends(dep_get_auth_service)],
):
    t0 = time.monotonic()
    await service.register_user(user_data, background_tasks)
    print(f"[ROUTER before return]: {time.monotonic() - t0:.2f}s")
    return {"message": "Confirmation code sent to email"}


@router.post("/login")
@limiter.limit(settings.limiter.login)
async def login_user(
    request: Request,  # it needs only for limiter
    user_data: LoginSchema,
    response: Response,
    service: Annotated[AuthService, Depends(dep_get_auth_service)],
):
    obj = await service.login_user(user_data)
    set_cookies(response, obj.id)
    return {"detail": "Logged in successfully"}


@router.post("/verify-email", response_model=UserSchemaRead)
async def verify_email(
    verify_data: VerifyEmailSchema,
    service: Annotated[AuthService, Depends(dep_get_auth_service)],
):
    return await service.verify_email(verify_data)


@router.post("/logout")
async def logout(response: Response):
    delete_cookies(response)
    return {"message": "Logged out successfully"}


@router.post("/refresh")
async def refresh(
    response: Response,
    refresh_token: Annotated[str, Depends(get_refresh_token)],
    service: Annotated[AuthService, Depends(dep_get_auth_service)],
):
    user_id: uuid = await service.refresh(refresh_token)
    set_cookies(response, user_id)
    return {"message": "Token successfully refreshed"}

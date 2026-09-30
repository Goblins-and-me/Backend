import uuid
from typing import Annotated, Callable

from domain.services import AuthService
from fastapi import Cookie, Depends, HTTPException, status
from infrastructure.clients import mail_service, redis
from infrastructure.database import CRUDUser, get_session
from sqlalchemy.ext.asyncio import AsyncSession


async def dep_get_auth_service(
    session: Annotated[AsyncSession, Depends(get_session)],
) -> AuthService:
    # if you wanna change CRUD you can do it only there
    # you do it like this because to create AuthService you need CRUDUser,
    # to create CRUDUser you need session but session comes only when we got a request from user
    return AuthService(
        crud=CRUDUser(session),
        redis=redis,
        mail=mail_service,
    )


def get_token_from_factory(cookie_name: str) -> Callable[[str | None], str]:
    def _get_token_through_cookie(
        token: Annotated[str | None, Cookie(alias=cookie_name)] = None,
    ) -> str:
        if not token:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="You have no permission",
            )
        return token

    return _get_token_through_cookie


get_access_token = get_token_from_factory("access_token")
get_refresh_token = get_token_from_factory("refresh_token")

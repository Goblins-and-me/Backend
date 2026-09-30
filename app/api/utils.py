import uuid

from core import jwt_creator, limiter, settings
from fastapi import FastAPI, Response
from slowapi import _rate_limit_exceeded_handler
from slowapi.errors import RateLimitExceeded


def register_limiter_handler(app: FastAPI) -> None:
    app.state.limiter = limiter
    app.add_exception_handler(
        RateLimitExceeded,
        _rate_limit_exceeded_handler,
    )


def delete_cookies(response: Response) -> None:
    response.delete_cookie(key="access_token")
    response.delete_cookie(key="refresh_token")


def set_cookies(response: Response, user_id: uuid.UUID) -> None:
    response.set_cookie(
        key="access_token",
        value=jwt_creator.create_access_token(user_id),
        httponly=True,
        # TODO: do not forget to change secure mode in prod
        secure=False,
        samesite="lax",
        max_age=settings.jwt.access_exp * 60,
    )

    response.set_cookie(
        key="refresh_token",
        value=jwt_creator.create_refresh_token(user_id),
        httponly=True,
        # TODO: do not forget to change secure mode in prod
        secure=False,  # secure=True
        samesite="lax",
        max_age=settings.jwt.refresh_exp * 24 * 60 * 60,
    )

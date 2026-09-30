import logging

from core.exceptions import (
    DatabaseError,
    IntegrityConstraintError,
    InvalidCodeError,
    InvalidCredentialsError,
    InvalidTokenTypeError,
    TokenValidationError,
    UserBlockedError,
)
from fastapi import FastAPI, Request, status
from starlette.responses import JSONResponse

log = logging.getLogger(__name__)


def register_exception_handlers(app: FastAPI):
    @app.exception_handler(InvalidCredentialsError)
    async def credentials_error(
        request: Request,
        exc: InvalidCredentialsError,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED, content={"detail": str(exc)}
        )

    @app.exception_handler(TokenValidationError)
    async def token_validation_error(
        request: Request,
        exc: TokenValidationError,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED, content={"detail": str(exc)}
        )

    @app.exception_handler(IntegrityConstraintError)
    async def integrity_error(
        request: Request,
        exc: IntegrityConstraintError,
    ):
        return JSONResponse(
            status_code=status.HTTP_409_CONFLICT, content={"detail": str(exc)}
        )

    @app.exception_handler(DatabaseError)
    async def database_error(request: Request, exc: DatabaseError):
        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={"detail": str(exc)},
        )

    @app.exception_handler(UserBlockedError)
    async def user_blocked_error(
        request: Request,
        exc: UserBlockedError,
    ):
        return JSONResponse(
            status_code=status.HTTP_403_FORBIDDEN,
            content={"detail": str(exc)},
        )

    @app.exception_handler(InvalidTokenTypeError)
    async def invalid_token_type_error(
        request: Request,
        exc: InvalidTokenTypeError,
    ):
        return JSONResponse(
            status_code=status.HTTP_401_UNAUTHORIZED, content={"detail": str(exc)}
        )

    @app.exception_handler(InvalidCodeError)
    async def invalid_code_error(
        request: Request,
        exc: InvalidCodeError,
    ):
        return JSONResponse(
            status_code=status.HTTP_400_BAD_REQUEST, content={"detail": str(exc)}
        )

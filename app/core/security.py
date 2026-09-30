import uuid
from datetime import datetime, timedelta, timezone
from typing import Any

import jwt
from argon2 import PasswordHasher
from argon2.exceptions import VerifyMismatchError
from core import settings
from jwt.exceptions import InvalidTokenError
from pydantic import SecretStr

from .exceptions import InvalidTokenTypeError, TokenValidationError

ph = PasswordHasher()


def hash_password(password: SecretStr) -> str:
    return ph.hash(password=password.get_secret_value())


def verify_password(hashed_password: str, password: str) -> bool:
    try:
        return ph.verify(hashed_password, password)
    except VerifyMismatchError:
        return False


class JWTCreator:
    @staticmethod
    def _create_base_token(
        user_id: uuid.UUID,
        token_type: str,
        expires_delta: timedelta,
    ) -> str:
        payload = {
            "sub": str(user_id),
            "exp": datetime.now(tz=timezone.utc) + expires_delta,
            "iat": datetime.now(tz=timezone.utc),
            "token_type": token_type,
        }
        return jwt.encode(
            payload=payload,
            key=settings.jwt.private_key,
            algorithm=settings.jwt.algorithm,
        )

    def create_access_token(self, user_id: uuid.UUID) -> str:
        return self._create_base_token(
            user_id,
            "access",
            timedelta(minutes=settings.jwt.access_exp),
        )

    def create_refresh_token(self, user_id: uuid.UUID) -> str:
        return self._create_base_token(
            user_id,
            "refresh",
            timedelta(days=settings.jwt.refresh_exp),
        )


jwt_creator = JWTCreator()


class JWTChecker:
    @staticmethod
    def _check_base_jwt(token: str, token_type: str) -> dict[str, Any]:
        try:
            payload = jwt.decode(
                jwt=token,
                key=settings.jwt.public_key,
                algorithms=[settings.jwt.algorithm],
            )
        except InvalidTokenError:
            raise TokenValidationError()
        if payload.get("token_type") != token_type:
            raise InvalidTokenTypeError()
        return payload

    def check_access_token(self, token: str) -> dict[str, Any]:
        return self._check_base_jwt(token, "access")

    def check_refresh_token(self, token: str) -> dict[str, Any]:
        return self._check_base_jwt(token, "refresh")


jwt_checker = JWTChecker()

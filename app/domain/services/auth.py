import hashlib
import json
import secrets
import time
import uuid

from core import (
    IntegrityConstraintError,
    InvalidCodeError,
    InvalidCredentialsError,
    TokenValidationError,
    UserBlockedError,
    security,
)
from infrastructure.clients import MailService
from infrastructure.database.crud.user import CRUDUser
from infrastructure.database.models import User
from redis.asyncio import Redis
from schemas import LoginSchema, UserSchemaAdd, VerifyEmailSchema


class AuthService:
    def __init__(self, *, crud: CRUDUser, mail: MailService, redis: Redis):
        self._crud = crud
        self._mail = mail
        self._redis = redis

    async def register_user(self, user_data: UserSchemaAdd, background_tasks) -> None:
        t0 = time.monotonic()
        user_dict = user_data.model_dump(exclude={"password_confirm"})
        email = user_dict["email"]
        if await self._crud.get_by_email(email):
            raise IntegrityConstraintError()
        print(f"[1] get_by_email: {time.monotonic() - t0:.2f}s")

        t1 = time.monotonic()
        user_dict["password"] = security.hash_password(user_dict.get("password"))
        print(f"[2] hash_password: {time.monotonic() - t1:.2f}s")

        t2 = time.monotonic()
        code = f"{secrets.randbelow(1_000_000):06d}"
        background_tasks.add_task(
            self._mail.send_confirmation_code_through_email, email, code
        )
        print(f"[3] add_task (should be instant!): {time.monotonic() - t2:.2f}s")

        t3 = time.monotonic()
        user_dict["code"] = code
        await self._redis.set(
            name=f"verify_email:{email}",
            value=json.dumps(user_dict),
            ex=300,
        )
        print(f"[4] redis.set: {time.monotonic() - t3:.2f}s")

        print(f"[TOTAL service method]: {time.monotonic() - t0:.2f}s")

    async def verify_email(self, verify_data: VerifyEmailSchema):
        json_data = await self._redis.get(f"verify_email:{verify_data.email}")
        if not json_data:
            raise InvalidCodeError()
        redis_data = json.loads(json_data)
        if verify_data.code != redis_data["code"]:
            raise InvalidCodeError()
        redis_data.pop("code")
        return await self._crud.create(redis_data)

    async def login_user(self, user_data: LoginSchema) -> User:
        obj = await self._crud.get_by_email(user_data.email)  # user_data.email
        # returns type EmailStr due to orm that run through model User even when it is select
        if obj is None:
            raise InvalidCredentialsError()
        if obj.is_blocked:
            raise UserBlockedError()
        if not security.verify_password(
            obj.password, user_data.password.get_secret_value()
        ):
            raise InvalidCredentialsError()
        return obj

    async def refresh(self, refresh_token: str) -> uuid.UUID:
        refresh_payload = security.jwt_checker.check_refresh_token(refresh_token)
        user_id = refresh_payload["sub"]
        user = await self._crud.get(user_id)
        if user is None:
            raise TokenValidationError()
        if user.is_blocked:
            raise UserBlockedError()
        the_rest_of_old_token_ttl = refresh_payload["exp"] - int(time.time())
        if the_rest_of_old_token_ttl <= 0:
            raise TokenValidationError()
        token_id = hashlib.sha256(refresh_token.encode()).hexdigest()
        if not await self._redis.set(
            name=f"blacklist:{token_id}",
            value=1,
            nx=True,
            ex=the_rest_of_old_token_ttl,
        ):
            raise TokenValidationError()
        return uuid.UUID(user_id)

    def _get_current_user_id(self, access_token: str) -> str:
        user_data = security.jwt_checker.check_access_token(access_token)
        return uuid.UUID(user_data.get("sub"))

    async def get_current_user(self, access_token: str) -> User:
        user_id = self._get_current_user_id(access_token)
        return await self._crud.get(user_id)

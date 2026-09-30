from pydantic import SecretStr
from schemas.user import UserschemaBase


class LoginSchema(UserschemaBase):
    password: SecretStr


class VerifyEmailSchema(UserschemaBase):
    code: str

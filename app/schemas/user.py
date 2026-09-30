import uuid
from datetime import datetime

from domain.enums import Role
from pydantic import BaseModel, EmailStr, SecretStr, field_validator, model_validator


# Base schema
class UserschemaBase(BaseModel):
    email: EmailStr


# This is an input shema to check some fields
class UserSchemaAdd(UserschemaBase):
    password: SecretStr
    password_confirm: SecretStr

    @field_validator("password")
    @classmethod
    def validate_password(cls, value: SecretStr) -> SecretStr:
        password = value.get_secret_value().strip()
        if len(password) < 10:
            raise ValueError("The password must contain at least 10 symbols")
        if not any(c.isupper() for c in password):
            raise ValueError("The password must contain at least one uppercase letter")
        if not any((c.islower() for c in password)):
            raise ValueError("The password must contain at least one lowercase letter")
        if not any(c.isdigit() for c in password):
            raise ValueError("The password must contain at least one digit")
        return value

    @model_validator(mode="after")
    def validate_passwords_match(self) -> "UserSchemaAdd":
        if self.password.get_secret_value() != self.password_confirm.get_secret_value():
            raise ValueError("Passwords do not match")
        return self


# This schema works when you give the client server answer
class UserSchemaRead(UserschemaBase):
    role: Role
    created_at: datetime
    id: uuid.UUID
    # TODO: you need to finish up this shema!!!!

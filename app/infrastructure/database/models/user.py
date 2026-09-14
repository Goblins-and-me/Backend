from sqlalchemy import Enum as SAEnum
from sqlalchemy import String, false
from sqlalchemy.orm import Mapped, mapped_column

from app.domain.enums import Role
from app.infrastructure.database.base import Base

from .mixins import CreatedAtMixin, IdPkMixin


class User(Base, IdPkMixin, CreatedAtMixin):
    email: Mapped[str] = mapped_column(
        String(255),
        unique=True,
    )
    password: Mapped[str] = mapped_column(String(255))
    is_blocked: Mapped[bool] = mapped_column(
        default=False,
        server_default=false(),
    )
    """
    Due to StrEnum, server_default calls str(Role.USER) when SQLAlchemy generates 
    the DDL — this happens on the Python side, before anything reaches Postgres.
    
    BUT there is one more issue I spotted too late:
    By default, SQLAlchemy resolves enum values from .name instead of .value.
    To fix this, use command below
    Enum(Role, values_callable=lambda x: [e.value for e in x])
    
    This also helps with the `default=` value. Despite the fact that in Python,
    Role.USER (as a StrEnum) already equals its .value when converted to str,
    SQLAlchemy's Enum type doesn't rely on that — by default it grabs .name
    for binding, not .value. So values_callable fixes this path too.
    """
    role: Mapped[Role] = mapped_column(
        SAEnum(Role, values_callable=lambda x: [e.value for e in x]),
        default=Role.USER,
        # Sqlalchemy calls __str__ method by default so we got .value
        server_default=Role.USER,
    )

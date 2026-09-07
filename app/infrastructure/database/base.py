import uuid

import uuid_utils
from sqlalchemy.orm import DeclarativeBase, Mapped, declared_attr, mapped_column


class Base(DeclarativeBase):
    __abstract__ = True

    # @declared_attr
    # def id(cls) -> Mapped[uuid.UUID]:
    #     return mapped_column(
    #         primary_key=True,
    #         default=uuid_utils.uuid7,
    #     )

    @declared_attr.directive
    def __tablename__(cls):
        return f"{cls.__name__.lower()}s"

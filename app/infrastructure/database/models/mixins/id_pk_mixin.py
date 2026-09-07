import uuid

import uuid_utils
from sqlalchemy.orm import Mapped, mapped_column


class IdPkMixin:
    id: Mapped[uuid.UUID] = mapped_column(primary_key=True, default=uuid_utils.uuid7)

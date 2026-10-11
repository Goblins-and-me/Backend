from datetime import datetime, timezone

from sqlalchemy import TIMESTAMP, func
from sqlalchemy.orm import Mapped, mapped_column


def utc_now() -> datetime:
    return datetime.now(tz=timezone.utc)


class UpdatedAtMixin:
    updated_at: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        default=utc_now,
        server_default=func.now(),
        onupdate=utc_now,
    )

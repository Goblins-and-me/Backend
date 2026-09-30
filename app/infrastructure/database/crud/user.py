import logging

from core import DatabaseError, IntegrityConstraintError
from pydantic import EmailStr
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError, SQLAlchemyError
from sqlalchemy.ext.asyncio import AsyncSession

from ..models import User

log = logging.getLogger(__name__)


class CRUDUser:
    def __init__(self, session: AsyncSession):
        self._session = session

    async def create(self, data: dict) -> User:
        try:
            obj = User(**data)
            self._session.add(obj)
            await self._session.commit()
            await self._session.refresh(obj)  # it is not necessary
            return obj
        except IntegrityError:
            await self._session.rollback()
            log.exception("Integrity error in create()")  # traceback included
            raise IntegrityConstraintError()
        except SQLAlchemyError:
            await self._session.rollback()
            log.exception("Database error in create()")
            raise DatabaseError()

    async def get(self, user_id) -> User | None:
        try:
            obj = await self._session.get(User, user_id)
            return obj
        # there is no point in writing IntegrityError due to get
        except SQLAlchemyError:
            await self._session.rollback()
            log.exception("Database error in get()")
            raise DatabaseError()

    async def get_by_email(self, email: EmailStr) -> User | None:
        try:
            query = select(User).where(User.email == email)
            obj = await self._session.execute(query)
            return obj.scalar_one_or_none()
        except SQLAlchemyError:
            await self._session.rollback()
            log.exception("Database error in get_by_email()")
            raise DatabaseError()

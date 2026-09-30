import logging

from sqlalchemy.ext.asyncio import create_async_engine

from app.core import settings

log = logging.getLogger(__name__)


async_engine = create_async_engine(
    url=str(settings.db.url),
    echo=settings.db.echo,
)


async def dispose():
    await async_engine.dispose()
    log.info("Dispose engine")

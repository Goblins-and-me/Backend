from sqlalchemy.ext.asyncio import async_sessionmaker

from .engine import async_engine

session = async_sessionmaker(
    bind=async_engine,
    autoflush=False,
    expire_on_commit=False,
)

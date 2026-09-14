from .session import session


async def get_session():
    async with session() as se:
        yield se

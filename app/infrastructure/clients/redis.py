from core import settings
from redis.asyncio import Redis

redis = Redis.from_url(
    url=str(settings.redis.url),
    decode_responses=True,
)

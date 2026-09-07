import logging

from .config import settings


def setup_root_logger(level: int):
    logging.basicConfig(
        format=settings.logging_conf.format,
        level=level,
    )

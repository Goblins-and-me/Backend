FROM python:3.13-slim

ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1

WORKDIR /app
ENV PYTHONPATH=/app/app

RUN pip install --no-cache-dir "poetry==2.4.1" \
    && poetry config virtualenvs.create false

COPY pyproject.toml poetry.lock ./
RUN poetry install --no-root --without dev

COPY alembic.ini ./
COPY alembic ./alembic
COPY app ./app

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

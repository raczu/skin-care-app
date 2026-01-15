import secrets
from typing import Literal

from pydantic import FilePath, PostgresDsn, ValidationInfo, field_validator
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    API_PATH: str = "/api/v1"
    ENVIRONMENT: Literal["development", "production"] = "development"

    JWT_ALGORITHM: Literal["HS256", "RS256"] = "HS256"
    ACCESS_TOKEN_SECRET_KEY: str = secrets.token_urlsafe(32)
    REFRESH_TOKEN_SECRET_KEY: str = secrets.token_urlsafe(32)
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    POSTGRES_USER: str
    POSTGRES_PASSWORD: str
    POSTGRES_HOST: str
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str
    ASYNC_POSTGRES_DSN: PostgresDsn | None = None
    SYNC_POSTGRES_DSN: PostgresDsn | None = None

    @field_validator("ASYNC_POSTGRES_DSN", mode="before")
    @classmethod
    def prepare_async_postgres_dsn(cls, v: str, info: ValidationInfo) -> PostgresDsn | str:
        if isinstance(v, str):
            return v
        return PostgresDsn.build(
            scheme="postgresql+asyncpg",
            username=info.data["POSTGRES_USER"],
            password=info.data["POSTGRES_PASSWORD"],
            host=info.data["POSTGRES_HOST"],
            port=info.data["POSTGRES_PORT"],
            path=f"{info.data['POSTGRES_DB'] or ''}",
        )

    @field_validator("SYNC_POSTGRES_DSN", mode="before")
    @classmethod
    def prepare_sync_postgres_dsn(cls, v: str, info: ValidationInfo) -> PostgresDsn | str:
        if isinstance(v, str):
            return v
        return PostgresDsn.build(
            scheme="postgresql+psycopg2",
            username=info.data["POSTGRES_USER"],
            password=info.data["POSTGRES_PASSWORD"],
            host=info.data["POSTGRES_HOST"],
            port=info.data["POSTGRES_PORT"],
            path=f"{info.data['POSTGRES_DB'] or ''}",
        )

    FCM_CREDENTIALS_PATH: FilePath
    NOTIFICATION_OFFSET_MINUTES: int = 15
    CELERY_BROKER_URL: str = "redis://localhost:6379/0"
    CELERY_RESULT_BACKEND: str = "redis://localhost:6379/0"


settings = Settings()

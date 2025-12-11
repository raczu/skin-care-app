from typing import Literal

from pydantic import PostgresDsn, ValidationInfo, field_validator
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    API_PATH: str = "/api/v1"
    ENVIRONMENT: Literal["development", "production"] = "development"

    POSTGRES_USER: str
    POSTGRES_PASSWORD: str
    POSTGRES_HOST: str
    POSTGRES_PORT: int = 5432
    POSTGRES_DB: str
    POSTGRES_DSN: PostgresDsn | None = None

    @field_validator("POSTGRES_DSN", mode="before")
    @classmethod
    def prepare_postgres_dsn(cls, v: str, info: ValidationInfo) -> PostgresDsn | str:
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


settings = Settings()

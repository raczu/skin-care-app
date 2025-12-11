import importlib.metadata

from fastapi import FastAPI

from app.api import router
from app.core import settings

app = FastAPI(
    title="skin-care-app",
    version=importlib.metadata.version("app"),
    openapi_url=f"{settings.API_PATH}/openapi.json",
    description="Note your skin care routines and get insights.",
)

app.include_router(router, prefix=settings.API_PATH)

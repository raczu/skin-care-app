import importlib.metadata

from fastapi import FastAPI
from fastapi.exceptions import HTTPException, RequestValidationError

from app.api import router
from app.core import settings
from app.core.exceptions import (
    RequirementMismatchError,
    http_exception_handler,
    validation_exception_handler,
)

app = FastAPI(
    title="skin-care-app",
    version=importlib.metadata.version("app"),
    openapi_url=f"{settings.API_PATH}/openapi.json",
    description="Note your skin care routines and get insights.",
)

app.add_exception_handler(HTTPException, http_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(RequirementMismatchError, validation_exception_handler)
app.include_router(router, prefix=settings.API_PATH)

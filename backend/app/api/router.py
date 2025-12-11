from fastapi import APIRouter

from app.api.routes import (
    auth_router,
    device_router,
    notification_router,
    private_router,
    product_router,
    routine_router,
    user_router,
)
from app.core import settings

router = APIRouter()
router.include_router(auth_router)
router.include_router(user_router)
router.include_router(device_router)
router.include_router(product_router)
router.include_router(routine_router)
router.include_router(notification_router)

if settings.ENVIRONMENT == "development":
    router.include_router(private_router)

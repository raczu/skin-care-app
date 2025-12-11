from app.api.routes.auth import router as auth_router
from app.api.routes.device import router as device_router
from app.api.routes.notification import router as notification_router
from app.api.routes.private import router as private_router
from app.api.routes.product import router as product_router
from app.api.routes.routine import router as routine_router
from app.api.routes.user import router as user_router

__all__ = [
    "auth_router",
    "private_router",
    "device_router",
    "notification_router",
    "product_router",
    "routine_router",
    "user_router",
]

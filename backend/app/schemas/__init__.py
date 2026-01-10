from app.schemas.common import (
    GenericMultipleItems,
    PaginatedResponse,
    PaginationMeta,
    PaginationParams,
)
from app.schemas.device import UserDevice, UserDeviceCreate
from app.schemas.notification import (
    CustomRule,
    CustomVariant,
    EveryNDaysRule,
    EveryNDaysVariant,
    NotificationRule,
    NotificationRuleCreate,
    NotificationRuleUpdate,
    SimpleRule,
    SimpleVariant,
)
from app.schemas.product import Product, ProductCreate, ProductUpdate
from app.schemas.routine import Routine, RoutineCreate, RoutineParams, RoutineUpdate
from app.schemas.token import RefreshToken, Token, TokenPayload
from app.schemas.user import User, UserCreate, UserUpdate

__all__ = [
    "User",
    "UserCreate",
    "UserUpdate",
    "Token",
    "TokenPayload",
    "RefreshToken",
    "UserDevice",
    "UserDeviceCreate",
    "PaginatedResponse",
    "PaginationParams",
    "GenericMultipleItems",
    "PaginationMeta",
    "Product",
    "ProductCreate",
    "ProductUpdate",
    "Routine",
    "RoutineCreate",
    "RoutineUpdate",
    "RoutineParams",
    "NotificationRuleCreate",
    "NotificationRule",
    "NotificationRuleUpdate",
    "CustomRule",
    "SimpleRule",
    "EveryNDaysRule",
    "CustomVariant",
    "EveryNDaysVariant",
    "SimpleVariant",
]

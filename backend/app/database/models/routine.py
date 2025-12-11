import uuid
from datetime import datetime
from enum import StrEnum

from sqlalchemy import DateTime, ForeignKey, String, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column

from app.database.models.base import Base


class RoutineType(StrEnum):
    MORNING = "MORNING"
    DAILY = "DAILY"
    NIGHT = "NIGHT"
    CUSTOM = "CUSTOM"


class Routine(Base):
    __tablename__ = "routines"

    routine_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4()
    )
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.user_id"))
    name: Mapped[str] = mapped_column(String(255), nullable=False)
    type: Mapped[RoutineType] = mapped_column(String(20), nullable=False)
    notes: Mapped[str]
    performed_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=func.now(), onupdate=func.now()
    )


class RoutineProduct(Base):
    __tablename__ = "routine_products"

    routine_product_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4()
    )
    routine_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("routines.routine_id"))
    product_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("products.product_id"))

import uuid
from datetime import datetime
from enum import StrEnum

from sqlalchemy import DateTime, ForeignKey, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.database.models.base import Base


class RoutineType(StrEnum):
    MORNING = "MORNING"
    DAILY = "DAILY"
    NIGHT = "NIGHT"
    OTHER = "OTHER"


class Routine(Base):
    __tablename__ = "routine"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("user.id"))
    type: Mapped[RoutineType] = mapped_column(String(20), nullable=False)
    notes: Mapped[str | None]
    performed_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=datetime.now, onupdate=datetime.now
    )

    products: Mapped[list["Product"]] = relationship(  # noqa: F821
        "Product", secondary="routine_product", lazy="joined"
    )


class RoutineProduct(Base):
    __tablename__ = "routine_product"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    routine_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("routine.id", ondelete="CASCADE"))
    product_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("product.id", ondelete="CASCADE"))

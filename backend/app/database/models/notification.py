import uuid
from datetime import datetime, time
from enum import StrEnum
from typing import Any

from sqlalchemy import ARRAY, DateTime, ForeignKey, SmallInteger, String, Time
from sqlalchemy.dialects.postgresql import JSONB, UUID
from sqlalchemy.orm import Mapped, mapped_column

from app.database.models.base import Base


class NotificationFrequency(StrEnum):
    ONCE = "ONCE"
    DAILY = "DAILY"
    EVERY_N_DAYS = "EVERY_N_DAYS"
    WEEKDAY_ONLY = "WEEKDAY_ONLY"
    CUSTOM = "CUSTOM"


class NotificationRule(Base):
    __tablename__ = "notification_rule"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("user.id"))
    time_of_day: Mapped[time] = mapped_column(Time(timezone=True), nullable=False)
    frequency: Mapped[NotificationFrequency] = mapped_column(String(20), nullable=False)
    every_n: Mapped[int] = mapped_column(nullable=True)
    weekdays: Mapped[list[int] | None] = mapped_column(ARRAY(SmallInteger), nullable=True)
    enabled: Mapped[bool] = mapped_column(nullable=False, default=True)
    next_run: Mapped[datetime | None] = mapped_column(DateTime(timezone=True), nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=datetime.now)
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=datetime.now, onupdate=datetime.now
    )


class NotificationStatus(StrEnum):
    PENDING = "PENDING"
    SENT = "SENT"
    FAILED = "FAILED"


class NotificationDelivery(Base):
    """
    Represents a specific instance of a notification attempt.
    Acts as audit log and a state holder for the workers.
    """

    __tablename__ = "notification_delivery"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    notification_rule_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("notification_rule.id"))
    scheduled_for: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    processed_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=True)
    status: Mapped[NotificationStatus] = mapped_column(
        String(20), nullable=False, default=NotificationStatus.PENDING
    )
    payload: Mapped[dict[str, Any]] = mapped_column(JSONB, nullable=False)
    provider_message_id: Mapped[str] = mapped_column(String, nullable=True)

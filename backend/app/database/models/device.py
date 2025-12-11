import uuid
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column

from app.database.models.base import Base


class UserDevice(Base):
    __tablename__ = "user_devices"

    device_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4()
    )
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("users.user_id"))
    fcm_token: Mapped[str] = mapped_column(nullable=False)
    registered_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())

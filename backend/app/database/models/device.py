import re
import uuid
from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, validates

from app.database.models.base import Base

META_PATTERN = re.compile(r"^(?:[a-zA-Z0-9_]+=[^;=]*;)*[a-zA-Z0-9_]+=[^;=]*$")


class UserDevice(Base):
    __tablename__ = "user_device"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4()
    )
    user_id: Mapped[uuid.UUID] = mapped_column(ForeignKey("user.id"))
    meta: Mapped[str] = mapped_column(nullable=False)
    fcm_token: Mapped[str] = mapped_column(nullable=False)
    registered_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())

    @validates("meta")
    def _validate_meta(self, key: str, value: str) -> str:
        if not META_PATTERN.match(value):
            raise ValueError("Meta must be in the format 'key1=value1;key2=value2;...'")
        return value

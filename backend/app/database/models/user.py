import re
import uuid
from datetime import datetime

from sqlalchemy import DateTime, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, validates

from app.database.models.base import Base

EMAIL_RE = re.compile(r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$")
USERNAME_RE = re.compile(r"^\w{4,}$")


class User(Base):
    __tablename__ = "user"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True), primary_key=True, default=uuid.uuid4
    )
    email: Mapped[str] = mapped_column(String(255), index=True, unique=True, nullable=False)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    surname: Mapped[str] = mapped_column(String(100), nullable=False)
    username: Mapped[str] = mapped_column(String(50), nullable=False)
    password: Mapped[str] = mapped_column(String(255), nullable=False)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=datetime.now)

    @validates("email")
    def _validate_email(self, key: str, address: str) -> str:
        if not EMAIL_RE.match(address):
            raise ValueError("Email address violates format requirements")
        return address

    @validates("username")
    def _validate_username(self, key: str, username: str) -> str:
        if not USERNAME_RE.match(username):
            raise ValueError("Username violates format requirements")
        return username

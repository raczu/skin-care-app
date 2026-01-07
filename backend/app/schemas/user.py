import re
import uuid
from datetime import datetime

from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator

PASSWORD_RE = re.compile(r"^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")


class UserBase(BaseModel):
    email: EmailStr
    name: str
    surname: str
    username: str


class UserCreate(UserBase):
    password: str

    @field_validator("password", mode="after")
    @classmethod
    def _validate_password(cls, v: str) -> str:
        if not PASSWORD_RE.match(v):
            raise ValueError(
                "Password must be at least 8 characters long, "
                "contain at least one uppercase letter, one lowercase letter, "
                "one digit, and one special character."
            )
        return v


class UserUpdate(BaseModel):
    email: str
    name: str
    surname: str


class UserInDB(UserBase):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID = Field(alias="user_id")
    created_at: datetime


class User(UserInDB): ...

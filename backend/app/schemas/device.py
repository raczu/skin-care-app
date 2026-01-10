import uuid
from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field, field_validator


class UserDeviceBase(BaseModel):
    meta: str = Field(..., description="Device metadata in 'key1=value1;key2=value2;...' format")

    @field_validator("meta", mode="after")
    @classmethod
    def _validate_meta(cls, v: str) -> str:
        if not v or any("=" not in pair for pair in v.split(";") if pair):
            raise ValueError("Meta must be in the format 'key1=value1;key2=value2;...'")
        return v


class UserDeviceCreate(UserDeviceBase):
    fcm_token: str


class UserDeviceInDB(UserDeviceBase):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID
    user_id: uuid.UUID
    registered_at: datetime


class UserDeviceRead(UserDeviceInDB): ...

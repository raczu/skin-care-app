import uuid
from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field


class ProductBase(BaseModel):
    name: str = Field(..., max_length=255)
    brand: str | None = Field(None, max_length=100)
    purpose: str | None = Field(None, max_length=500)
    description: str | None = None


class ProductCreate(ProductBase): ...


class ProductUpdate(ProductBase):
    name: str | None = Field(None, max_length=255)


class ProductInDB(ProductBase):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID
    user_id: uuid.UUID
    created_at: datetime
    updated_at: datetime


class Product(ProductInDB): ...

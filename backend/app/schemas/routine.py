import uuid
from datetime import datetime, timezone
from typing import Self

from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator

from app.database.models.routine import RoutineType
from app.schemas.common import PaginationParams
from app.schemas.product import ProductRead


class RoutineParams(PaginationParams):
    performed_after: datetime | None = Field(
        None, description="Time after which this routine was performed (timezone-aware)"
    )
    performed_before: datetime | None = Field(
        None, description="Time before which this routine was performed (timezone-aware)"
    )


class RoutineBase(BaseModel):
    type: RoutineType
    notes: str | None = None
    performed_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))


class RoutineCreate(RoutineBase):
    type: RoutineType | None = None
    product_ids: list[uuid.UUID]

    @model_validator(mode="after")
    def prepare_routine_type(self) -> Self:
        if self.type is None:
            hour = self.performed_at.astimezone().hour
            if 5 <= hour < 12:
                self.type = RoutineType.MORNING
            elif 12 <= hour < 18:
                self.type = RoutineType.DAILY
            else:
                self.type = RoutineType.NIGHT
        return self


class RoutineUpdatePartial(RoutineBase):
    type: RoutineType | None = None
    product_ids: list[uuid.UUID] | None = None
    performed_at: datetime | None = None

    @field_validator("product_ids")
    @classmethod
    def _validate_products(cls, v: list[uuid.UUID] | None) -> list[uuid.UUID] | None:
        if v is not None and len(v) == 0:
            raise ValueError("Routine must have at least one product")
        return v


class RoutineInDB(RoutineBase):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID
    user_id: uuid.UUID
    created_at: datetime
    updated_at: datetime
    products: list[ProductRead]


class RoutineRead(RoutineInDB): ...

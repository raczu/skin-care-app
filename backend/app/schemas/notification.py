import uuid
from datetime import datetime, time
from typing import Annotated, Literal, Self

from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator

from app.database.models.notification import NotificationFrequency


class NotificationRuleBase(BaseModel):
    time_of_day: time = Field(
        ..., description="Time of day when notification should be sent (timezone-aware)"
    )

    @field_validator("time_of_day")
    @classmethod
    def _validate_tz_aware(cls, v: time) -> time:
        if v.tzinfo is None:
            raise ValueError("Time of day must be timezone-aware")
        return v


class EveryNDaysVariant(NotificationRuleBase):
    frequency: Literal[NotificationFrequency.EVERY_N_DAYS]
    every_n: int = Field(..., ge=1, description="Interval in days")
    weekdays: None = None


class CustomVariant(NotificationRuleBase):
    frequency: Literal[NotificationFrequency.CUSTOM]
    every_n: None = None
    weekdays: list[int] = Field(
        ...,
        description="Fixed-size array of 7 elements (monday to sunday) with 0 or 1",
    )

    @field_validator("weekdays")
    @classmethod
    def _validate_weekdays(cls, v: list[int] | None) -> list[int] | None:
        if v is not None:
            if len(v) != 7:
                raise ValueError("Weekdays must be exactly 7 elements long")
            if any(d not in (0, 1) for d in v):
                raise ValueError("Weekdays must contain only 0 or 1 values")
        return v


class SimpleVariant(NotificationRuleBase):
    frequency: Literal[
        NotificationFrequency.ONCE,
        NotificationFrequency.DAILY,
        NotificationFrequency.WEEKDAY_ONLY,
    ]
    every_n: None = None
    weekdays: None = None


NotificationRuleCreate = Annotated[
    EveryNDaysVariant | CustomVariant | SimpleVariant, Field(discriminator="frequency")
]


class NotificationRuleUpdatePartial(BaseModel):
    time_of_day: time | None = None
    frequency: NotificationFrequency | None = None
    every_n: int | None = None
    weekdays: list[int] | None = None
    enabled: bool | None = None

    @model_validator(mode="after")
    def _validate_frequency(self) -> Self:
        if self.frequency is None:
            return self
        if self.frequency == NotificationFrequency.EVERY_N_DAYS and self.every_n is None:
            raise ValueError("Value for days interval is required when frequency is EVERY_N_DAYS")
        if self.frequency == NotificationFrequency.CUSTOM and not self.weekdays:
            raise ValueError("Weekdays are required when frequency is CUSTOM")
        return self


class NotificationRuleInDB(NotificationRuleBase):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID
    user_id: uuid.UUID
    every_n: int | None = None
    weekdays: list[int] | None = None
    enabled: bool
    next_run: datetime | None = None
    created_at: datetime
    updated_at: datetime


class EveryNDaysRule(EveryNDaysVariant, NotificationRuleInDB): ...


class CustomRule(CustomVariant, NotificationRuleInDB): ...


class SimpleRule(SimpleVariant, NotificationRuleInDB): ...


NotificationRuleRead = Annotated[
    EveryNDaysRule | CustomRule | SimpleRule, Field(discriminator="frequency")
]

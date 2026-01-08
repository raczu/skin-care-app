from typing import Generic, TypeVar

from pydantic import BaseModel, Field
from pydantic.generics import GenericModel

M = TypeVar("M", bound=BaseModel)


class GenericMultipleItems(GenericModel, Generic[M]):
    items: list[M]


class PaginationParams(BaseModel):
    limit: int = Field(default=15, gt=0, le=100)
    offset: int = Field(default=0, ge=0)


class PaginationMeta(BaseModel):
    total: int = Field(..., ge=0)
    count: int = Field(..., ge=0)


class PaginatedResponse(GenericMultipleItems[M]):
    meta: PaginationMeta
    pagination: PaginationParams

from typing import Generic, TypeVar

from pydantic import BaseModel, Field
from pydantic.generics import GenericModel

M = TypeVar("M", bound=BaseModel)


class GenericMultipleItems(GenericModel, Generic[M]):
    items: list[M]


class PaginationParams(BaseModel):
    limit: int = Field(
        default=15, gt=0, le=100, description="Maximum number of items to return per page (1-100)"
    )
    offset: int = Field(
        default=0,
        ge=0,
        description="Number of items to skip before starting to collect the result set",
    )


class PaginationMeta(BaseModel):
    total: int = Field(
        ..., ge=0, description="Total number of items matching the query across all pages"
    )
    count: int = Field(
        ..., ge=0, description="Number of items actually returned in the page (<= limit)"
    )


class PaginatedResponse(GenericMultipleItems[M], Generic[M]):
    meta: PaginationMeta
    pagination: PaginationParams

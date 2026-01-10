import uuid
from typing import Awaitable, Callable, TypeVar

from fastapi import HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.schemas import PaginatedResponse, PaginationMeta, PaginationParams

M = TypeVar("M")


def paginate(items: list[M], total: int, limit: int, offset: int) -> PaginatedResponse[M]:
    """Utility function to create a paginated response."""
    return PaginatedResponse[M](
        items=items,
        meta=PaginationMeta(total=total, count=len(items)),
        pagination=PaginationParams(limit=limit, offset=offset),
    )


async def get_owned_resource_or_404(
    session: AsyncSession,
    getter: Callable[[AsyncSession, uuid.UUID], Awaitable[M | None]],
    resource_id: uuid.UUID,
    owner_id: uuid.UUID,
    detail: str = "Resource not found or you don't have access to it",
) -> M:
    """
    Utility function to retrieve a resource by ID and verify ownership.
    """
    resource = await getter(session, resource_id)
    if resource is None or resource.user_id != owner_id:
        raise HTTPException(status_code=404, detail=detail)
    return resource

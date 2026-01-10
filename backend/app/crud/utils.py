from typing import TypeVar

from sqlalchemy import Select, func, select
from sqlalchemy.ext.asyncio import AsyncSession

T = TypeVar("T")


async def get_paginated_resources(
    session: AsyncSession, statement: Select[tuple[T]], limit: int, offset: int
) -> tuple[list[T], int]:
    """Retrieve paginated resources based on the provided SQLAlchemy statement.

    Returns:
        A tuple containing a list of resources and the total count of resources.
    """
    total = await session.scalar(select(func.count()).select_from(statement.subquery()))
    total = total or 0

    statement = statement.offset(offset).limit(limit)
    result = await session.execute(statement)
    resources = result.scalars().all()
    return list(resources), total

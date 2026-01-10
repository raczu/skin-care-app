import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.crud.product import get_user_products_by_ids
from app.crud.utils import get_paginated_resources
from app.database.models import Routine
from app.schemas import RoutineCreate, RoutineParams, RoutineUpdatePartial


async def get_user_routines(
    session: AsyncSession, user_id: uuid.UUID, params: RoutineParams | None = None
) -> tuple[list[Routine], int]:
    """Retrieve routines for a specific user with optional filtering and pagination.

    Returns:
        A tuple containing a list of Routine instances and the total count of routines.
    """
    if params is None:
        params = RoutineParams()
    statement = (
        select(Routine).where(Routine.user_id == user_id).options(selectinload(Routine.products))
    )

    if params.performed_after is not None:
        statement = statement.where(Routine.performed_at >= params.performed_after)
    if params.performed_before is not None:
        statement = statement.where(Routine.performed_at <= params.performed_before)

    statement = statement.order_by(Routine.performed_at.desc())
    routines, total = await get_paginated_resources(
        session, statement, limit=params.limit, offset=params.offset
    )
    return list(routines), total


async def get_routine_by_id(session: AsyncSession, id: uuid.UUID) -> Routine | None:
    routine = await session.get(Routine, id)
    return routine


async def create_routine(
    session: AsyncSession, user_id: uuid.UUID, routine_in: RoutineCreate
) -> Routine:
    routine = Routine(
        user_id=user_id,
        type=routine_in.type,
        notes=routine_in.notes,
        performed_at=routine_in.performed_at,
    )
    products = await get_user_products_by_ids(session, user_id, routine_in.product_ids)
    routine.products = products

    session.add(routine)
    await session.flush()
    await session.refresh(routine)
    return routine


async def update_routine(
    session: AsyncSession, routine: Routine, routine_in: RoutineUpdatePartial
) -> Routine:
    data = routine_in.model_dump(exclude_unset=True)
    for field, value in data.items():
        setattr(routine, field, value)
    if routine_in.product_ids:
        products = await get_user_products_by_ids(session, routine.user_id, routine_in.product_ids)
        routine.products = products

    await session.flush()
    await session.refresh(routine)
    return routine


async def delete_routine(session: AsyncSession, routine: Routine) -> None:
    await session.delete(routine)
    await session.flush()

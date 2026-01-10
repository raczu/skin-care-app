import uuid
from typing import Annotated

from fastapi import APIRouter, HTTPException, Query, Response, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.api.utils import get_owned_resource_or_404, paginate
from app.database.models import Routine
from app.schemas import (
    PaginatedResponse,
    RoutineCreate,
    RoutineParams,
    RoutineRead,
    RoutineUpdatePartial,
)

router = APIRouter(prefix="/routines", tags=["routines"])

ROUTINE_NOT_FOUND_MESSAGE = "Routine not found or you don't have access to it"


@router.post(
    "/",
    summary="Create a new performed routine entry for the current user",
    status_code=status.HTTP_201_CREATED,
    response_model=RoutineRead,
)
async def create_routine(
    *, routine_in: RoutineCreate, session: SessionDep, user: CurrentUserDep
) -> Routine:
    products = await crud.product.get_user_products_by_ids(session, user.id, routine_in.product_ids)
    if {p.id for p in products} != set(routine_in.product_ids):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="One or more products do not exist in the user's inventory",
        )
    routine = await crud.routine.create_routine(session, user.id, routine_in)
    return routine


@router.get("/", summary="Get all performed routine entries for the current user")
async def get_routines(
    params: Annotated[RoutineParams, Query()], *, session: SessionDep, user: CurrentUserDep
) -> PaginatedResponse[RoutineRead]:
    routines, total = await crud.routine.get_user_routines(session, user.id, params)
    return paginate(items=routines, total=total, limit=params.limit, offset=params.offset)


@router.get("/{id}", summary="Get a specific performed routine entry", response_model=RoutineRead)
async def get_routine_by_id(id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep) -> Routine:
    routine = await get_owned_resource_or_404(
        session=session,
        getter=crud.routine.get_routine_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=ROUTINE_NOT_FOUND_MESSAGE,
    )
    return routine


@router.patch(
    "/{id}", summary="Update a specific performed routine entry", response_model=RoutineRead
)
async def update_routine_by_id(
    id: uuid.UUID, *, routine_in: RoutineUpdatePartial, session: SessionDep, user: CurrentUserDep
) -> Routine:
    if routine_in.product_ids is not None:
        products = await crud.product.get_user_products_by_ids(
            session, user.id, routine_in.product_ids
        )
        if {p.id for p in products} != set(routine_in.product_ids):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="One or more products do not exist in the user's inventory",
            )

    routine = await get_owned_resource_or_404(
        session=session,
        getter=crud.routine.get_routine_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=ROUTINE_NOT_FOUND_MESSAGE,
    )

    routine = await crud.routine.update_routine(session, routine, routine_in)
    return routine


@router.delete(
    "/{id}",
    summary="Delete a specific performed routine entry",
    status_code=status.HTTP_204_NO_CONTENT,
)
async def delete_routine_by_id(
    id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep
) -> Response:
    routine = await get_owned_resource_or_404(
        session=session,
        getter=crud.routine.get_routine_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=ROUTINE_NOT_FOUND_MESSAGE,
    )
    await crud.routine.delete_routine(session, routine)
    return Response(status_code=status.HTTP_204_NO_CONTENT)

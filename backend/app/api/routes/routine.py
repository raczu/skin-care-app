import uuid

from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/routines", tags=["routines"])


@router.post("/", summary="Create a new performed routine entry for the current user")
async def create_routine(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/", summary="Get all performed routine entries for the current user")
async def get_routines(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/{routine_id}", summary="Get a specific performed routine entry")
async def get_routine_by_id(routine_id: uuid.UUID, *, session: SessionDep) -> Response:
    raise NotImplementedError


@router.put("/{routine_id}", summary="Update a specific performed routine entry")
async def update_routine_by_id(routine_id: uuid.UUID, *, session: SessionDep) -> Response:
    raise NotImplementedError


@router.delete("/{routine_id}", summary="Delete a specific performed routine entry")
async def delete_routine_by_id(routine_id: uuid.UUID, *, session: SessionDep) -> Response:
    raise NotImplementedError

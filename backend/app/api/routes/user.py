from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/users", tags=["users"])


@router.post("/", summary="Create a new user")
async def create_user(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/me", summary="Get current user information")
async def get_user_me(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.put("/me", summary="Update current user information")
async def update_user_me(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/me/stats", summary="Get current user statistics related to skin care activities")
async def get_user_stats_me(*, session: SessionDep) -> Response:
    raise NotImplementedError

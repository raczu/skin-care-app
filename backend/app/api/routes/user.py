from fastapi import APIRouter, HTTPException, Response, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.database.models import User
from app.schemas import UserCreate, UserRead, UserUpdatePartial

router = APIRouter(prefix="/users", tags=["users"])


@router.post(
    "/", summary="Create a new user", response_model=UserRead, status_code=status.HTTP_201_CREATED
)
async def create_user(*, user_in: UserCreate, session: SessionDep) -> User:
    user = await crud.user.get_user_by_email(session, str(user_in.email))
    if user is not None:
        raise HTTPException(status_code=400, detail="The user with this email already exists")
    user = await crud.user.create_user(session, user_in)
    return user


@router.get("/me", summary="Get current user information", response_model=UserRead)
async def get_user_me(user: CurrentUserDep) -> User:
    return user


@router.patch("/me", summary="Update current user information", response_model=UserRead)
async def update_user_me(
    *, user_in: UserUpdatePartial, session: SessionDep, user: CurrentUserDep
) -> User:
    user = await crud.user.update_user(session, user, user_in)
    return user


@router.get("/me/stats", summary="Get current user statistics related to skin care activities")
async def get_user_stats_me(session: SessionDep, user: CurrentUserDep) -> Response:
    raise NotImplementedError

from datetime import timedelta
from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm

from app import crud
from app.api.deps import ParseJWTRefreshTokenDep, SessionDep
from app.core import create_access_token, create_refresh_token, settings
from app.schemas import Token

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/token", summary="Get an access token for user authentication")
async def get_access_token(
    session: SessionDep, form: Annotated[OAuth2PasswordRequestForm, Depends()]
) -> Token:
    user = await crud.user.authenticate_user(session, form.username, form.password)
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    fullname = f"{user.name} {user.surname}"
    access_token = create_access_token(
        subject=str(user.user_id),
        name=fullname,
        delta=timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES),
    )
    refresh_token = create_refresh_token(
        subject=str(user.user_id),
        name=fullname,
        delta=timedelta(days=settings.REFRESH_TOKEN_EXPIRE_DAYS),
    )
    return Token(
        access_token=access_token,
        refresh_token=refresh_token,
        expires_in=settings.ACCESS_TOKEN_EXPIRE_MINUTES * 60,
    )


@router.post("/refresh", summary="Refresh an access token using a refresh token")
async def refresh_access_token(payload: ParseJWTRefreshTokenDep) -> Token:
    access_token = create_access_token(
        subject=payload.sub,
        name=payload.name,
        delta=timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES),
    )
    refresh_token = create_refresh_token(
        subject=payload.sub,
        name=payload.name,
        delta=timedelta(days=settings.REFRESH_TOKEN_EXPIRE_DAYS),
    )
    return Token(
        access_token=access_token,
        refresh_token=refresh_token,
        expires_in=settings.ACCESS_TOKEN_EXPIRE_MINUTES * 60,
    )

import uuid
from typing import Annotated, AsyncGenerator

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import JWTError, jwt
from pydantic import ValidationError
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.ext.asyncio import AsyncSession

from app.core import settings
from app.database import AsyncSessionLocal
from app.database.models import User
from app.schemas import RefreshToken, TokenPayload

REUSABLE_OAUTH2 = OAuth2PasswordBearer(
    tokenUrl=f"{settings.API_PATH}/auth/token", refreshUrl=f"{settings.API_PATH}/auth/refresh"
)


async def get_async_db() -> AsyncGenerator[AsyncSession, None]:
    async with AsyncSessionLocal() as session:
        try:
            yield session
            await session.commit()
        except SQLAlchemyError:
            await session.rollback()
            raise
        finally:
            await session.close()


SessionDep = Annotated[AsyncSession, Depends(get_async_db)]
TokenDep = Annotated[str, Depends(REUSABLE_OAUTH2)]


def parse_jwt_token(token: TokenDep) -> TokenPayload:
    try:
        payload = jwt.decode(
            token, settings.ACCESS_TOKEN_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM]
        )
        data = TokenPayload(**payload)
    except (JWTError, ValidationError) as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc
    return data


ParseJWTTokenDep = Annotated[TokenPayload, Depends(parse_jwt_token)]


def parse_jwt_refresh_token(body: RefreshToken) -> TokenPayload:
    token = body.refresh_token
    try:
        payload = jwt.decode(
            token, settings.REFRESH_TOKEN_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM]
        )
        data = TokenPayload(**payload)
    except (JWTError, ValidationError) as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid refresh token",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc
    return data


ParseJWTRefreshTokenDep = Annotated[TokenPayload, Depends(parse_jwt_refresh_token)]


async def get_current_user(session: SessionDep, payload: ParseJWTTokenDep) -> User:
    user: User | None = await session.get(User, uuid.UUID(payload.sub))
    if user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found",
            headers={"WWW-Authenticate": "Bearer"},
        )
    return user


CurrentUserDep = Annotated[User, Depends(get_current_user)]

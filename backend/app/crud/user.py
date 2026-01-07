import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core import get_password_hash, verify_password
from app.database.models import User
from app.schemas import UserCreate, UserUpdate


async def get_user_by_id(session: AsyncSession, id: uuid.UUID) -> User | None:
    user = await session.get(User, id)
    return user


async def get_user_by_email(session: AsyncSession, email: str) -> User | None:
    result = await session.execute(select(User).where(User.email == email))
    return result.scalar_one_or_none()


async def create_user(session: AsyncSession, user_in: UserCreate) -> User:
    user = User(
        email=str(user_in.email),
        name=user_in.name,
        surname=user_in.surname,
        username=user_in.username,
        password=get_password_hash(user_in.password),
    )
    session.add(user)
    await session.flush()
    await session.refresh(user)
    return user


async def update_user(session: AsyncSession, user: User, user_in: UserUpdate) -> User:
    user.email = user_in.email or user.email
    user.name = user_in.name or user.name
    user.surname = user_in.surname or user.surname
    await session.flush()
    await session.refresh(user)
    return user


async def authenticate_user(session: AsyncSession, email: str, password: str) -> User | None:
    user = await get_user_by_email(session, email)
    if user is not None:
        mismatch = not verify_password(password, user.password)
        if mismatch:
            user = None
    return user

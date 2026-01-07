from datetime import datetime, timedelta, timezone

from jose import jwt
from passlib.context import CryptContext

from app.core.settings import settings

PWD_CONTEXT = CryptContext(schemes=["bcrypt"], deprecated="auto")


def _create_token(subject: str, name: str, delta: timedelta, key: str) -> str:
    now = datetime.now(timezone.utc)
    expire = now + delta
    to_encode = {"exp": expire, "sub": subject, "name": name, "iat": now}
    encoded_jwt = jwt.encode(to_encode, key, algorithm=settings.JWT_ALGORITHM)
    return encoded_jwt


def create_access_token(subject: str, name: str, delta: timedelta) -> str:
    return _create_token(subject, name, delta, settings.ACCESS_TOKEN_SECRET_KEY)


def create_refresh_token(subject: str, name: str, delta: timedelta) -> str:
    return _create_token(subject, name, delta, settings.REFRESH_TOKEN_SECRET_KEY)


def verify_password(plain: str, hashed: str) -> bool:
    return PWD_CONTEXT.verify(plain, hashed)


def get_password_hash(password: str) -> str:
    return PWD_CONTEXT.hash(password)

from typing import Literal

from pydantic import BaseModel


class Token(BaseModel):
    access_token: str
    refresh_token: str
    token_type: Literal["bearer"] = "bearer"
    expires_in: int


class RefreshToken(BaseModel):
    refresh_token: str


class TokenPayload(BaseModel):
    sub: str
    name: str
    exp: int
    iat: int

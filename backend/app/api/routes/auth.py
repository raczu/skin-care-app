from fastapi import APIRouter, Response

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/token", summary="Get an access token for user authentication")
async def get_access_token() -> Response:
    raise NotImplementedError

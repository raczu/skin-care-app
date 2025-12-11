from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/devices", tags=["devices"])


@router.post("/", summary="Register a device for push notifications for the current user")
async def register_device(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/", summary="Get registered device information for the current user")
async def get_device_info_me(*, session: SessionDep) -> Response:
    raise NotImplementedError

from typing import Any

from fastapi import APIRouter, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.schemas import GenericMultipleItems, UserDevice, UserDeviceCreate

router = APIRouter(prefix="/devices", tags=["devices"])


@router.post(
    "/",
    summary="Register a device for push notifications for the current user",
    status_code=status.HTTP_201_CREATED,
    response_model=UserDevice,
)
async def register_device(
    *, device_in: UserDeviceCreate, session: SessionDep, user: CurrentUserDep
) -> UserDevice:
    device = await crud.device.create_user_device(session, user.id, device_in)
    return device


@router.get(
    "/",
    summary="Get registered device information for the current user",
    response_model=GenericMultipleItems[UserDevice],
)
async def get_device_info_me(session: SessionDep, user: CurrentUserDep) -> dict[str, Any]:
    devices = await crud.device.get_user_devices(session, user.id)
    return {"items": devices}

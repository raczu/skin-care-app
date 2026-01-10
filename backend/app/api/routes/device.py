from fastapi import APIRouter, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.database.models import UserDevice
from app.schemas import GenericMultipleItems, UserDeviceCreate, UserDeviceRead

router = APIRouter(prefix="/devices", tags=["devices"])


@router.post(
    "/",
    summary="Register a device for push notifications for the current user",
    status_code=status.HTTP_201_CREATED,
    response_model=UserDeviceRead,
)
async def register_device(
    *, device_in: UserDeviceCreate, session: SessionDep, user: CurrentUserDep
) -> UserDevice:
    device = await crud.device.create_user_device(session, user.id, device_in)
    return device


@router.get(
    "/",
    summary="Get registered device information for the current user",
    response_model=GenericMultipleItems[UserDeviceRead],
)
async def get_devices(
    session: SessionDep, user: CurrentUserDep
) -> GenericMultipleItems[UserDeviceRead]:
    devices = await crud.device.get_user_devices(session, user.id)
    return GenericMultipleItems[UserDeviceRead](items=devices)

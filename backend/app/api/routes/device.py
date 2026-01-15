from fastapi import APIRouter, HTTPException, status

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
    device = await crud.device.get_device_by_fcm_token(session, device_in.fcm_token)
    if device is not None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Device with this FCM token is already registered",
        )
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

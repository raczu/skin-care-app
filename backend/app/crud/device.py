import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database.models import UserDevice
from app.schemas import UserDeviceCreate


async def get_user_devices(session: AsyncSession, user_id: uuid.UUID) -> list[UserDevice]:
    result = await session.execute(select(UserDevice).where(UserDevice.user_id == user_id))
    devices = result.scalars().all()
    return list(devices)


async def create_user_device(
    session: AsyncSession, user_id: uuid.UUID, device_in: UserDeviceCreate
) -> UserDevice:
    device = UserDevice(
        user_id=user_id,
        fcm_token=device_in.fcm_token,
        meta=device_in.meta,
    )
    session.add(device)
    await session.flush()
    await session.refresh(device)
    return device

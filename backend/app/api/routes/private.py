from contextlib import suppress

from fastapi import APIRouter, Response
from firebase_admin.exceptions import FirebaseError

from app import crud
from app.api.deps import SessionDep
from app.services.fcm import FCMService

router = APIRouter(prefix="/private", tags=["private"])


@router.post(
    "/test-notify-all", summary="Send general push notification for all registered devices"
)
async def notify(*, session: SessionDep) -> Response:
    devices = await crud.device.get_all_user_devices(session)
    for device in devices:
        with suppress(FirebaseError):
            FCMService.send_message(
                token=device.fcm_token,
                title="Test Notification",
                body="This is a test notification sent to all registered devices.",
            )
    return Response(status_code=204)

from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/private", tags=["private"])


@router.post(
    "/test-notify-all", summary="Send general push notification for all registered devices"
)
async def notify(*, session: SessionDep) -> Response:
    raise NotImplementedError

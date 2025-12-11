import uuid

from fastapi import APIRouter, Response

from app.api.deps import SessionDep

router = APIRouter(prefix="/notification-rules", tags=["notification-rules"])


@router.post("/", summary="Create a new notification rule for the current user")
async def create_notification_rule(*, session: SessionDep) -> Response:
    raise NotImplementedError


@router.get("/{notification_rule_id}", summary="Get a specific notification rule")
async def get_notification_rule(
    notification_rule_id: uuid.UUID,
    *,
    session: SessionDep,
) -> Response:
    raise NotImplementedError


@router.put("/{notification_rule_id}", summary="Update a specific notification rule")
async def update_notification_rule(
    notification_rule_id: uuid.UUID,
    *,
    session: SessionDep,
) -> Response:
    raise NotImplementedError


@router.delete("/{notification_rule_id}", summary="Delete a specific notification rule")
async def delete_notification_rule(
    notification_rule_id: uuid.UUID,
    *,
    session: SessionDep,
) -> Response:
    raise NotImplementedError

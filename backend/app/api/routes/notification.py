import uuid

from fastapi import APIRouter, Response, status

from app import crud
from app.api.deps import CurrentUserDep, SessionDep
from app.api.utils import get_owned_resource_or_404
from app.schemas import (
    GenericMultipleItems,
    NotificationRule,
    NotificationRuleCreate,
    NotificationRuleUpdate,
)

router = APIRouter(prefix="/notification-rules", tags=["notification-rules"])

NOTIFICATION_RULE_NOT_FOUND_MESSAGE = "Notification rule not found or you don't have access to it"


@router.post(
    "/",
    summary="Create a new notification rule for the current user",
    response_model=NotificationRule,
)
async def create_notification_rule(
    *, rule_in: NotificationRuleCreate, session: SessionDep, user: CurrentUserDep
) -> NotificationRule:
    rule = await crud.notification.create_notification_rule(session, user.id, rule_in)
    return rule


@router.get(
    "/",
    summary="Get all notification rules for the current user",
    response_model=GenericMultipleItems[NotificationRule],
)
async def get_notification_rules(
    *, session: SessionDep, user: CurrentUserDep
) -> GenericMultipleItems[NotificationRule]:
    rules = await crud.notification.get_user_notification_rules(session, user.id)
    return GenericMultipleItems[NotificationRule](items=rules)


@router.get("/{id}", summary="Get a specific notification rule", response_model=NotificationRule)
async def get_notification_rule(
    id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep
) -> NotificationRule:
    rule = await get_owned_resource_or_404(
        session=session,
        getter=crud.notification.get_notification_rule_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=NOTIFICATION_RULE_NOT_FOUND_MESSAGE,
    )
    return rule


@router.patch(
    "/{id}", summary="Update a specific notification rule", response_model=NotificationRule
)
async def update_notification_rule(
    id: uuid.UUID, *, rule_in: NotificationRuleUpdate, session: SessionDep, user: CurrentUserDep
) -> NotificationRule:
    rule = await get_owned_resource_or_404(
        session=session,
        getter=crud.notification.get_notification_rule_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=NOTIFICATION_RULE_NOT_FOUND_MESSAGE,
    )
    rule = await crud.notification.update_notification_rule(session, rule, rule_in)
    return rule


@router.delete(
    "/{id}", summary="Delete a specific notification rule", status_code=status.HTTP_204_NO_CONTENT
)
async def delete_notification_rule(
    id: uuid.UUID, *, session: SessionDep, user: CurrentUserDep
) -> Response:
    rule = await get_owned_resource_or_404(
        session=session,
        getter=crud.notification.get_notification_rule_by_id,
        resource_id=id,
        owner_id=user.id,
        detail=NOTIFICATION_RULE_NOT_FOUND_MESSAGE,
    )
    await crud.notification.delete_notification_rule(session, rule)
    return Response(status_code=status.HTTP_204_NO_CONTENT)

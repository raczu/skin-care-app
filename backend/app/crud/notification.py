import uuid

from pydantic import TypeAdapter, ValidationError
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.exceptions import RequirementMismatchError
from app.database.models import NotificationRule
from app.schemas import NotificationRuleCreate, NotificationRuleUpdatePartial
from app.services import NotificationScheduler


async def get_user_notification_rules(
    session: AsyncSession, user_id: uuid.UUID
) -> list[NotificationRule]:
    result = await session.execute(
        select(NotificationRule).where(NotificationRule.user_id == user_id)
    )
    rules = result.scalars().all()
    return list(rules)


async def get_notification_rule_by_id(
    session: AsyncSession, id: uuid.UUID
) -> NotificationRule | None:
    rule = await session.get(NotificationRule, id)
    return rule


async def create_notification_rule(
    session: AsyncSession, user_id: uuid.UUID, rule_in: NotificationRuleCreate
) -> NotificationRule:
    rule = NotificationRule(
        user_id=user_id,
        time_of_day=rule_in.time_of_day,
        frequency=rule_in.frequency,
        every_n=rule_in.every_n,
        weekdays=rule_in.weekdays,
    )
    session.add(rule)
    await session.flush()
    await session.refresh(rule)
    rule.next_run = NotificationScheduler.plan_next_run(rule, last_run=None)
    return rule


async def update_notification_rule(
    session: AsyncSession, rule: NotificationRule, rule_in: NotificationRuleUpdatePartial
) -> NotificationRule:
    """Update a notification rule and validate requirements.

    Raises:
        RequirementMismatchError: If the updated notification rule does not meet the requirements
            of any notification rule variant.
    """
    data = rule_in.model_dump(exclude_unset=True)
    for field, value in data.items():
        setattr(rule, field, value)

    # After entity update, validate against NotificationRuleCreate to check
    # whether it still meets any variants' requirements.
    try:
        _ = TypeAdapter(NotificationRuleCreate).validate_python(rule, from_attributes=True)
    except ValidationError as exc:
        raise RequirementMismatchError(errors=exc.errors()) from exc
    rule.next_run = NotificationScheduler.plan_next_run(rule, last_run=None)

    await session.flush()
    await session.refresh(rule)
    return rule


async def delete_notification_rule(session: AsyncSession, rule: NotificationRule) -> None:
    await session.delete(rule)
    await session.flush()

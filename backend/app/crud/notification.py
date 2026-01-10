import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database.models import NotificationRule
from app.schemas import NotificationRuleCreate, NotificationRuleUpdatePartial


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
    return rule


async def update_notification_rule(
    session: AsyncSession, rule: NotificationRule, rule_in: NotificationRuleUpdatePartial
) -> NotificationRule:
    rule.time_of_day = rule_in.time_of_day
    rule.frequency = rule_in.frequency
    rule.every_n = rule_in.every_n
    rule.weekdays = rule_in.weekdays
    rule.enabled = rule_in.enabled

    # TODO: Revalidate fields against rules requirements (EveryNDaysVariant, etc.)

    session.add(rule)
    await session.flush()
    await session.refresh(rule)
    return rule


async def delete_notification_rule(session: AsyncSession, rule: NotificationRule) -> None:
    await session.delete(rule)
    await session.flush()

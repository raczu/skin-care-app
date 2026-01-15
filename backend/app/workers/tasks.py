import uuid
from collections import defaultdict
from datetime import datetime, timezone
from typing import Any, override

from celery import Task
from firebase_admin.exceptions import FirebaseError
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.core import celery
from app.database import get_sync_db_context
from app.database.models import (
    NotificationDelivery,
    NotificationRule,
    NotificationStatus,
    UserDevice,
)
from app.schemas import NotificationTask
from app.services import FCMService, NotificationScheduler

DUE_NOTIFICATIONS_BATCH_SIZE = 1000


def prepare_notification_tasks_for_rules(
    session: Session, rules: list[NotificationRule], devices_by_user: dict[str, list[str]]
) -> list[NotificationTask]:
    tasks = []
    for rule in rules:
        fcm_tokens = devices_by_user.get(str(rule.user_id), [])
        rule.next_run = NotificationScheduler.plan_next_run(rule, last_run=rule.next_run)  # type: ignore
        if rule.next_run is None:
            rule.enabled = False

        for fcm_token in fcm_tokens:
            payload = {
                "title": "Time for your routine!",
                "body": "Your scheduled skin care treatment is due soon. Let's get that glow!",
            }
            delivery = NotificationDelivery(
                notification_rule_id=rule.id,
                status=NotificationStatus.PENDING,
                scheduled_for=datetime.now(timezone.utc),
                payload=payload,
            )
            session.add(delivery)
            session.flush()
            session.refresh(delivery)

            tasks.append(
                NotificationTask(
                    delivery_id=delivery.id,
                    user_fcm_token=fcm_token,
                    title=payload["title"],
                    body=payload["body"],
                )
            )
    return tasks


def process_due_notifications_batch(
    session: Session, rule_ids: list[uuid.UUID]
) -> list[NotificationTask]:
    statement = select(NotificationRule).where(NotificationRule.id.in_(rule_ids)).with_for_update()
    rules = session.execute(statement).scalars().all()

    user_ids = {rule.user_id for rule in rules}
    devices_by_user = defaultdict(list)
    devices = (
        session.query(UserDevice.user_id, UserDevice.fcm_token)
        .filter(UserDevice.user_id.in_(user_ids))
        .all()
    )
    for user_id, fcm_token in devices:
        devices_by_user[str(user_id)].append(fcm_token)

    return prepare_notification_tasks_for_rules(session, list(rules), devices_by_user)


@celery.task(name="dispatch_due_notifications")
def dispatch_due_notifications() -> None:
    with get_sync_db_context() as session:
        now = datetime.now(timezone.utc)
        statement = (
            select(NotificationRule.id)
            .where(NotificationRule.enabled)
            .where(NotificationRule.next_run <= now)
            .with_for_update()
        )
        rules_ids = session.execute(statement).scalars().all()

    for idx in range(0, len(rules_ids), DUE_NOTIFICATIONS_BATCH_SIZE):
        batch_rule_ids = rules_ids[idx : idx + DUE_NOTIFICATIONS_BATCH_SIZE]
        with get_sync_db_context() as session:
            tasks_to_dispatch = process_due_notifications_batch(session, batch_rule_ids)

        for task in tasks_to_dispatch:
            send_fcm_notification.delay(task.model_dump())


class NotificationDBTask(Task):
    @override
    def on_success(self, retval: Any, task_id: str, args: tuple, kwargs: dict[str, Any]) -> None:
        data = args[0] if args else kwargs.get("data", {})
        if data:
            task = NotificationTask(**data)
            self._update_delivery_status(
                delivery_id=task.delivery_id, status=NotificationStatus.SENT
            )

    @override
    def on_failure(
        self, exc: Exception, task_id: str, args: tuple, kwargs: dict[str, Any], einfo: Any
    ) -> None:
        data = args[0] if args else kwargs.get("data", {})
        if data:
            task = NotificationTask(**data)
            self._update_delivery_status(
                delivery_id=task.delivery_id, status=NotificationStatus.FAILED
            )

    @staticmethod
    def _update_delivery_status(
        delivery_id: uuid.UUID,
        status: NotificationStatus,
        provider_message_id: str | None = None,
    ) -> None:
        with get_sync_db_context() as session:
            delivery = session.get(NotificationDelivery, delivery_id)
            if delivery and delivery.processed_at is None:
                delivery.status = status
                delivery.processed_at = datetime.now(timezone.utc)
                delivery.provider_message_id = provider_message_id


@celery.task(
    name="send_fcm_notification",
    base=NotificationDBTask,
    bind=True,
    max_retries=3,
    default_retry_delay=60,
    autoretry_for=(FirebaseError,),
)
def send_fcm_notification(data: dict[str, Any]) -> str | None:
    task = NotificationTask(**data)
    with get_sync_db_context() as session:
        delivery = session.get(NotificationDelivery, task.delivery_id)
        if delivery is None:
            return None
        if delivery.status == NotificationStatus.SENT:
            return delivery.provider_message_id

        return FCMService.send_message(
            token=task.user_fcm_token,
            title=task.title,
            body=task.body,
        )

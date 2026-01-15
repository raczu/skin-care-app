from celery import Celery
from celery.schedules import crontab

from app.core.settings import settings

celery = Celery(
    "worker",
    broker=settings.CELERY_BROKER_URL,
    backend=settings.CELERY_RESULT_BACKEND,
    include=["app.workers.tasks"],
)

celery.conf.update(
    task_serializer="json",
    accept_content=["json"],
    result_serializer="json",
    timezone="UTC",
    enable_utc=True,
    task_track_started=True,
    task_acks_late=True,
    task_default_queue="default",
    task_routes={
        "dispatch_due_notifications": {"queue": "periodic"},
        "send_fcm_notification": {"queue": "notifications"},
    },
)

celery.conf.beat_schedule = {
    "dispatch-notifications-every-minute": {
        "task": "dispatch_due_notifications",
        "schedule": crontab(minute="*"),
    },
}

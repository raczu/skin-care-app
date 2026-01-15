import logging

import firebase_admin
from firebase_admin import credentials, messaging
from firebase_admin.exceptions import FirebaseError

from app.core import settings
from app.schemas import NotificationMetadata

logger = logging.getLogger(__name__)


class FCMService:
    _initialized: bool = False

    @classmethod
    def _ensure_initialized(cls) -> None:
        if not cls._initialized:
            cred = credentials.Certificate(settings.FCM_SERVICE_ACCOUNT_JSON)
            try:
                firebase_admin.get_app()
            except ValueError:
                firebase_admin.initialize_app(cred)
            cls._initialized = True

    @classmethod
    def send_message(
        cls, token: str, title: str, body: str, metadata: NotificationMetadata | None = None
    ) -> str:
        """Sends a Firebase Cloud Messaging (FCM) message to a specific device.

        Returns:
            The message ID string if the message was sent successfully.
        """
        cls._ensure_initialized()
        stringified = {}
        if metadata:
            stringified = {
                key: str(value) for key, value in metadata.model_dump(exclude_none=True).items()
            }
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            data=stringified,
            token=token,
        )

        try:
            return messaging.send(message)
        except FirebaseError as exc:
            logger.error("Failed to send FCM message %s", message, exc_info=True)
            raise exc

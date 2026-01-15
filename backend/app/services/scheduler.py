from abc import ABC, abstractmethod
from datetime import datetime, time, timedelta, timezone
from typing import override

from app.core import settings
from app.core.exceptions import ConfigurationError, InvalidFrequencyError
from app.database.models import NotificationFrequency, NotificationRule


class SchedulingStrategy(ABC):
    @abstractmethod
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None
    ) -> datetime | None:
        """Calculates the next run datetime based on the rule and the last run time."""
        raise NotImplementedError

    @staticmethod
    def _get_execution_time(target_date: datetime, rule_time: time) -> datetime:
        event_dt = datetime.combine(target_date.date(), rule_time)
        event_dt = event_dt.replace(tzinfo=timezone.utc)
        return event_dt - timedelta(minutes=settings.NOTIFICATION_OFFSET_MINUTES)


class OnceStrategy(SchedulingStrategy):
    @override
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None
    ) -> datetime | None:
        if last_run is not None:
            return None
        now = datetime.now(timezone.utc)
        candidate = self._get_execution_time(now, rule.time_of_day)
        return candidate if candidate > now else None


class DailyStrategy(SchedulingStrategy):
    @override
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None = None
    ) -> datetime | None:
        now = datetime.now(timezone.utc)
        base_date = last_run if last_run is not None else now
        candidate = self._get_execution_time(base_date, rule.time_of_day)

        while candidate <= now:
            candidate += timedelta(days=1)
            candidate = self._get_execution_time(candidate, rule.time_of_day)
        return candidate


class EveryNDaysStrategy(SchedulingStrategy):
    @override
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None = None
    ) -> datetime | None:
        now = datetime.now(timezone.utc)
        if not rule.every_n:
            raise ConfigurationError("Every n days frequency requires value for interval")

        if last_run is None:
            candidate = self._get_execution_time(now, rule.time_of_day)
            if candidate <= now:
                target_date = now + timedelta(days=rule.every_n)
                candidate = self._get_execution_time(target_date, rule.time_of_day)
            return candidate
        next_date_base = last_run + timedelta(days=rule.every_n)
        return self._get_execution_time(next_date_base, rule.time_of_day)


class WeekdayOnlyStrategy(SchedulingStrategy):
    @override
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None = None
    ) -> datetime | None:
        now = datetime.now(timezone.utc)
        base_date = last_run if last_run is not None else now
        candidate = self._get_execution_time(base_date, rule.time_of_day)

        while candidate <= now or candidate.weekday() >= 5:
            candidate += timedelta(days=1)
            candidate = self._get_execution_time(candidate, rule.time_of_day)
        return candidate


class CustomStrategy(SchedulingStrategy):
    """Strategy for user-selected custom weekdays."""

    _DAYS_AHEAD_CHECK: int = 15

    @override
    def get_next_occurrence(
        self, rule: NotificationRule, last_run: datetime | None = None
    ) -> datetime | None:
        if not rule.weekdays or len(rule.weekdays) != 7:
            raise ConfigurationError("Custom frequency requires a valid 7-day binary mask")
        now = datetime.now(timezone.utc)
        base_date = last_run if last_run is not None else now

        for days_ahead in range(self._DAYS_AHEAD_CHECK):
            target_date_raw = base_date + timedelta(days=days_ahead)
            candidate = self._get_execution_time(target_date_raw, rule.time_of_day)
            if candidate <= now:
                continue
            if rule.weekdays[candidate.weekday()] == 1:
                return candidate
        return None


class NotificationScheduler:
    _STRATEGIES: dict[NotificationFrequency, SchedulingStrategy] = {
        NotificationFrequency.ONCE: OnceStrategy(),
        NotificationFrequency.DAILY: DailyStrategy(),
        NotificationFrequency.EVERY_N_DAYS: EveryNDaysStrategy(),
        NotificationFrequency.WEEKDAY_ONLY: WeekdayOnlyStrategy(),
        NotificationFrequency.CUSTOM: CustomStrategy(),
    }

    @classmethod
    def plan_next_run(
        cls, rule: NotificationRule, last_run: datetime | None = None
    ) -> datetime | None:
        strategy = cls._STRATEGIES.get(rule.frequency)
        if strategy is None:
            raise InvalidFrequencyError("Unsupported notification frequency was provided")
        return strategy.get_next_occurrence(rule, last_run)

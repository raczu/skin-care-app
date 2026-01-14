package com.raczu.skincareapp.data.domain.models.notification

import java.time.LocalTime

sealed class NotificationRuleCreate {
    abstract val timeOfDay: LocalTime
    abstract val frequency: NotificationFrequency

    data class Once(override val timeOfDay: LocalTime) : NotificationRuleCreate() {
        override val frequency = NotificationFrequency.ONCE
    }

    data class Daily(override val timeOfDay: LocalTime) : NotificationRuleCreate() {
        override val frequency = NotificationFrequency.DAILY
    }

    data class WeekdayOnly(override val timeOfDay: LocalTime) : NotificationRuleCreate() {
        override val frequency = NotificationFrequency.WEEKDAY_ONLY
    }

    data class EveryNDays(
        override val timeOfDay: LocalTime,
        val everyN: Int
    ) : NotificationRuleCreate() {
        override val frequency = NotificationFrequency.EVERY_N_DAYS
    }

    data class Custom(
        override val timeOfDay: LocalTime,
        val weekdays: List<Int>
    ) : NotificationRuleCreate() {
        override val frequency = NotificationFrequency.CUSTOM
    }
}
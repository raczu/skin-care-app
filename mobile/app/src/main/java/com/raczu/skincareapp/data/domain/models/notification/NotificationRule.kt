package com.raczu.skincareapp.data.domain.models.notification

import java.time.LocalTime

sealed class NotificationRule {
    abstract val id: String
    abstract val timeOfDay: LocalTime
    abstract val enabled: Boolean
    abstract val frequency: NotificationFrequency

    data class Once(
        override val id: String,
        override val timeOfDay: LocalTime,
        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.ONCE
    ) : NotificationRule()

    data class Daily(
        override val id: String,
        override val timeOfDay: LocalTime,
        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.DAILY
    ) : NotificationRule()

    data class WeekdayOnly(
        override val id: String,
        override val timeOfDay: LocalTime,
        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.WEEKDAY_ONLY
    ) : NotificationRule()

    data class EveryNDays(
        override val id: String,
        override val timeOfDay: LocalTime,
        override val enabled: Boolean,
        val everyN: Int,
        override val frequency: NotificationFrequency = NotificationFrequency.EVERY_N_DAYS
    ) : NotificationRule()

    data class Custom(
        override val id: String,
        override val timeOfDay: LocalTime,
        override val enabled: Boolean,
        val weekdays: List<Int>,
        override val frequency: NotificationFrequency = NotificationFrequency.CUSTOM
    ) : NotificationRule()
}

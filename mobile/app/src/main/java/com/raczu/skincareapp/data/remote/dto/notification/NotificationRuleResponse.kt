package com.raczu.skincareapp.data.remote.dto.notification

import com.google.gson.annotations.SerializedName
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import java.time.OffsetTime

sealed class NotificationRuleResponse {
    abstract val id: String

    @get:SerializedName("time_of_day")
    abstract val timeOfDay: OffsetTime

    abstract val enabled: Boolean
    abstract val frequency: NotificationFrequency

    data class Once(
        override val id: String,

        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.ONCE
    ) : NotificationRuleResponse()

    data class Daily(
        override val id: String,

        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.DAILY
    ) : NotificationRuleResponse()

    data class WeekdayOnly(
        override val id: String,

        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        override val enabled: Boolean,
        override val frequency: NotificationFrequency = NotificationFrequency.WEEKDAY_ONLY
    ) : NotificationRuleResponse()

    data class EveryNDays(
        override val id: String,

        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        override val enabled: Boolean,

        @SerializedName("every_n")
        val everyN: Int,

        override val frequency: NotificationFrequency = NotificationFrequency.EVERY_N_DAYS
    ) : NotificationRuleResponse()

    data class Custom(
        override val id: String,

        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        override val enabled: Boolean,
        val weekdays: List<Int>,
        override val frequency: NotificationFrequency = NotificationFrequency.CUSTOM
    ) : NotificationRuleResponse()
}
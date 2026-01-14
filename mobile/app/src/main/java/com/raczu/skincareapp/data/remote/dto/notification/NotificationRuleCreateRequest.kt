package com.raczu.skincareapp.data.remote.dto.notification

import com.google.gson.annotations.SerializedName
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import java.time.OffsetTime

sealed class NotificationRuleCreateRequest {
    @get:SerializedName("time_of_day")
    abstract val timeOfDay: OffsetTime

    data class Once(
        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,
    ) : NotificationRuleCreateRequest()

    data class Daily(
        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,
    ) : NotificationRuleCreateRequest()

    data class WeekdayOnly(
        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,
    ) : NotificationRuleCreateRequest()

    data class EveryNDays(
        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        @SerializedName("every_n")
        val everyN: Int,
    ) : NotificationRuleCreateRequest()

    data class Custom(
        @SerializedName("time_of_day")
        override val timeOfDay: OffsetTime,

        val weekdays: List<Int>,
    ) : NotificationRuleCreateRequest()
}

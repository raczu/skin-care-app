package com.raczu.skincareapp.data.remote.dto.notification

import com.google.gson.annotations.SerializedName
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import java.time.OffsetTime

data class NotificationRuleUpdateRequest(
    @SerializedName("time_of_day")
    val timeOfDay: OffsetTime? = null,

    val frequency: NotificationFrequency? = null,

    @SerializedName("every_n")
    val everyN: Int? = null,

    val weekdays: List<Int>? = null,
    val enabled: Boolean? = null
)

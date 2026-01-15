package com.raczu.skincareapp.data.domain.models.notification

import java.time.LocalTime

data class NotificationRuleUpdate(
    val timeOfDay: LocalTime? = null,
    val frequency: NotificationFrequency? = null,
    val everyN: Int? = null,
    val weekdays: List<Int>? = null,
    val enabled: Boolean? = null
)

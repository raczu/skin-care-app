package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleCreate
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import com.raczu.skincareapp.data.remote.ExplicitNull
import com.raczu.skincareapp.data.remote.api.toDeviceLocalTime
import com.raczu.skincareapp.data.remote.api.toUtcOffsetTime
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleCreateRequest
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleResponse
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleUpdateRequest

fun NotificationRuleResponse.toDomain(): NotificationRule {
    val localTime = this.timeOfDay.toDeviceLocalTime()
    return when (this) {
        is NotificationRuleResponse.Once -> NotificationRule.Once(id, localTime, enabled)
        is NotificationRuleResponse.Daily -> NotificationRule.Daily(id, localTime, enabled)
        is NotificationRuleResponse.WeekdayOnly -> NotificationRule.WeekdayOnly(id, localTime, enabled)
        is NotificationRuleResponse.EveryNDays -> NotificationRule.EveryNDays(id, localTime, enabled, everyN)
        is NotificationRuleResponse.Custom -> NotificationRule.Custom(id, localTime, enabled, weekdays)
    }
}

fun NotificationRuleCreate.toRequest(): NotificationRuleCreateRequest {
    val offsetTime = this.timeOfDay.toUtcOffsetTime()
    return when (this) {
        is NotificationRuleCreate.Once -> NotificationRuleCreateRequest.Once(offsetTime)
        is NotificationRuleCreate.Daily -> NotificationRuleCreateRequest.Daily(offsetTime)
        is NotificationRuleCreate.WeekdayOnly -> NotificationRuleCreateRequest.WeekdayOnly(offsetTime)

        is NotificationRuleCreate.EveryNDays -> NotificationRuleCreateRequest.EveryNDays(
            timeOfDay = offsetTime,
            everyN = this.everyN
        )
        is NotificationRuleCreate.Custom -> NotificationRuleCreateRequest.Custom(
            timeOfDay = offsetTime,
            weekdays = this.weekdays
        )
    }
}

fun NotificationRuleUpdate.toRequest(): NotificationRuleUpdateRequest {
    return NotificationRuleUpdateRequest(
        timeOfDay = this.timeOfDay?.toUtcOffsetTime(),
        frequency = this.frequency,
        everyN = ExplicitNull(this.everyN),
        weekdays = ExplicitNull(this.weekdays),
        enabled = this.enabled
    )
}

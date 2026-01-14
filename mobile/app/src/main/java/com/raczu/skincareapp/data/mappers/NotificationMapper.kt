package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleCreate
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleCreateRequest
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleResponse
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleUpdateRequest
import java.time.ZoneOffset

fun NotificationRuleResponse.toDomain(): NotificationRule {
    val localTime = this.timeOfDay.toLocalTime()
    return when (this) {
        is NotificationRuleResponse.Once -> NotificationRule.Once(id, localTime, enabled)
        is NotificationRuleResponse.Daily -> NotificationRule.Daily(id, localTime, enabled)
        is NotificationRuleResponse.WeekdayOnly -> NotificationRule.WeekdayOnly(id, localTime, enabled)
        is NotificationRuleResponse.EveryNDays -> NotificationRule.EveryNDays(id, localTime, enabled, everyN)
        is NotificationRuleResponse.Custom -> NotificationRule.Custom(id, localTime, enabled, weekdays)
    }
}

fun NotificationRuleCreate.toRequest(): NotificationRuleCreateRequest {
    val offsetTime = this.timeOfDay.atOffset(ZoneOffset.UTC)
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
        timeOfDay = this.timeOfDay?.atOffset(ZoneOffset.UTC),
        frequency = this.frequency,
        everyN = this.everyN,
        weekdays = this.weekdays,
        enabled = this.enabled
    )
}

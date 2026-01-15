package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleCreate
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import kotlinx.coroutines.flow.StateFlow

interface NotificationRuleRepository : CleanableRepository {
    val notifications: StateFlow<List<NotificationRule>>

    suspend fun getNotificationRules(): Result<List<NotificationRule>>
    suspend fun getNotificationRuleDetails(ruleId: String): Result<NotificationRule>
    suspend fun addNotificationRule(rule: NotificationRuleCreate): Result<NotificationRule>
    suspend fun updateNotificationRule(
        ruleId: String, rule: NotificationRuleUpdate
    ): Result<NotificationRule>
    suspend fun deleteNotificationRule(ruleId: String): Result<Unit>
}
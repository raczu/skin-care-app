package com.raczu.skincareapp.data.repository

import android.util.Log
import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleCreate
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import com.raczu.skincareapp.data.mappers.toDomain
import com.raczu.skincareapp.data.mappers.toRequest
import com.raczu.skincareapp.data.remote.api.NotificationRuleApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteNotificationRuleRepository(
    private val notificationRuleApiService: NotificationRuleApiService
) : NotificationRuleRepository {
    private val _notifications = MutableStateFlow<List<NotificationRule>>(emptyList())
    override val notifications: StateFlow<List<NotificationRule>> = _notifications.asStateFlow()

    override suspend fun addNotificationRule(rule: NotificationRuleCreate): Result<NotificationRule> {
        val request = rule.toRequest()
        val result = safeApiCall { notificationRuleApiService.addNotification(request) }

        return result.map { response ->
            val newRule = response.toDomain()
            _notifications.update { curr -> listOf(newRule) + curr }
            newRule
        }
    }

    override suspend fun getNotificationRules(): Result<List<NotificationRule>> {
        val result = safeApiCall { notificationRuleApiService.getNotificationRules() }
        return result.map { response ->
            val domainRules = response.items.map { it.toDomain() }
            _notifications.update { domainRules }
            domainRules
        }
    }

    override suspend fun deleteNotificationRule(ruleId: String): Result<Unit> {
        val result = safeApiCall { notificationRuleApiService.deleteNotificationRule(ruleId) }
        if (result.isSuccess) {
            _notifications.update { curr ->
                curr.filterNot { it.id == ruleId }
            }
        }
        return result
    }

    override suspend fun updateNotificationRule(
        ruleId: String,
        rule: NotificationRuleUpdate
    ): Result<NotificationRule> {
        val request = rule.toRequest()
        val result = safeApiCall {
            notificationRuleApiService.updateNotificationRule(ruleId, request)
        }

        return result.map { response ->
            val updatedRule = response.toDomain()
            _notifications.update { curr ->
                curr.map { if (it.id == ruleId) updatedRule else it }
            }
            updatedRule
        }
    }

    override suspend fun getNotificationRuleDetails(ruleId: String): Result<NotificationRule> {
        val result = safeApiCall { notificationRuleApiService.getNotificationRuleDetails(ruleId) }
        return result.map { response ->
            val details = response.toDomain()
            _notifications.update { curr ->
                curr.map { if(it.id == ruleId) details else it }
            }
            details
        }
    }

    override fun clearData() {
        _notifications.update { emptyList() }
    }
}
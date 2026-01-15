package com.raczu.skincareapp.ui.features.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import com.raczu.skincareapp.data.repository.DeviceTokenRepository
import com.raczu.skincareapp.data.repository.NotificationRuleRepository
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutineNotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationRule> = emptyList(),
    val error: String? = null
)

data class RoutineNotificationsLoadState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class RoutineNotificationsViewModel(
    private val notificationRuleRepository: NotificationRuleRepository,
    private val deviceTokenRepository: DeviceTokenRepository
) : ViewModel() {
    private val _loadState = MutableStateFlow(RoutineNotificationsLoadState())
    val uiState: StateFlow<RoutineNotificationsUiState> = combine(
        notificationRuleRepository.notifications,
        _loadState
    ) { notifications, loadState ->
        RoutineNotificationsUiState(
            isLoading = loadState.isLoading,
            notifications = notifications,
            error = loadState.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = RoutineNotificationsUiState(isLoading = true)
    )

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        if (_loadState.value.isLoading) return
        viewModelScope.launch {
            _loadState.update { it.copy(isLoading = true, error = null) }

            val result = notificationRuleRepository.getNotificationRules()
            result.onSuccess { rules ->
                _loadState.update { it.copy(isLoading = false) }
            }.onFailure { error ->
                _loadState.update { it.copy(isLoading = false, error = error.toUiErrorMessage()) }
            }
        }
    }

    fun toggleNotification(ruleId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val update = NotificationRuleUpdate(enabled = isEnabled)
            val result = notificationRuleRepository.updateNotificationRule(
                ruleId, update
            )
            result.onFailure { exception ->
                _loadState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun deleteNotification(ruleId: String) {
        viewModelScope.launch {
            val result = notificationRuleRepository.deleteNotificationRule(ruleId)
            result.onFailure { exception ->
                _loadState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun onNotificationPermissionGranted() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                viewModelScope.launch {
                    deviceTokenRepository.saveCurrentToken(token)
                        .onFailure {
                            Log.d("RoutineNotificationsVM", "Failed to save device token: ${it.message}")
                        }
                }
            }
        }
    }
}
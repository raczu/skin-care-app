package com.raczu.skincareapp.ui.features.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.notification.NotificationFrequency
import com.raczu.skincareapp.data.domain.models.notification.NotificationRule
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleCreate
import com.raczu.skincareapp.data.domain.models.notification.NotificationRuleUpdate
import com.raczu.skincareapp.data.domain.validation.rules.MinValueValidator
import com.raczu.skincareapp.data.domain.validation.rules.NoOpValidator
import com.raczu.skincareapp.data.domain.validation.rules.WeekdayMaskValidator
import com.raczu.skincareapp.data.repository.NotificationRuleRepository
import com.raczu.skincareapp.ui.common.FormFieldState
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

data class RoutineNotificationFromUiState(
    val isLoading: Boolean = false,
    val isOperationSuccessful: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)

class RoutineNotificationFormViewModel(
    private val notificationRuleRepository: NotificationRuleRepository,
    private val ruleId: String? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        RoutineNotificationFromUiState(isEditMode = ruleId != null)
    )
    val uiState: StateFlow<RoutineNotificationFromUiState> = _uiState.asStateFlow()

    class RoutineNotificationFormFields(onChanged: () -> Unit) {
        val time = FormFieldState(
            initialValue = LocalTime.now(),
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        val frequency = FormFieldState(
            initialValue = NotificationFrequency.DAILY,
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        val everyN = FormFieldState(
            initialValue = 1,
            validator = MinValueValidator(1),
            onValueChangeCallback = onChanged
        )
        val weekdays = FormFieldState(
            initialValue = List(7) { 0 },
            validator = WeekdayMaskValidator(),
            onValueChangeCallback = onChanged
        )

        fun validateAll(): Boolean {
            return when (frequency.value) {
                NotificationFrequency.EVERY_N_DAYS -> everyN.validate()
                NotificationFrequency.CUSTOM -> weekdays.validate()
                else -> true
            }
        }

        fun toNotificationRuleCreate(): NotificationRuleCreate {
            return when (frequency.value) {
                NotificationFrequency.ONCE -> NotificationRuleCreate.Once(
                    timeOfDay = time.value
                )
                NotificationFrequency.DAILY -> NotificationRuleCreate.Daily(
                    timeOfDay = time.value
                )
                NotificationFrequency.WEEKDAY_ONLY -> NotificationRuleCreate.WeekdayOnly(
                    timeOfDay = time.value
                )
                NotificationFrequency.EVERY_N_DAYS -> NotificationRuleCreate.EveryNDays(
                    timeOfDay = time.value,
                    everyN = everyN.value
                )
                NotificationFrequency.CUSTOM -> NotificationRuleCreate.Custom(
                    timeOfDay = time.value,
                    weekdays = weekdays.value
                )
            }
        }

        fun toNotificationRuleUpdate(): NotificationRuleUpdate {
            return NotificationRuleUpdate(
                timeOfDay = time.value,
                frequency = frequency.value,
                everyN = if (frequency.value == NotificationFrequency.EVERY_N_DAYS) everyN.value else null,
                weekdays = if (frequency.value == NotificationFrequency.CUSTOM) weekdays.value else null
            )
        }
    }

    private val onFieldChanged = {
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
    val fields = RoutineNotificationFormFields(onChanged = onFieldChanged)

    init {
        initializeForm()
    }

    fun initializeForm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            if (ruleId != null) {
                loadNotificationRuleDetails(ruleId)
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadNotificationRuleDetails(ruleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = notificationRuleRepository.getNotificationRuleDetails(ruleId)
            result.onSuccess { notification ->
                fields.time.value = notification.timeOfDay
                fields.frequency.value = notification.frequency
                if (notification is NotificationRule.EveryNDays) {
                    fields.everyN.value = notification.everyN
                }
                if (notification is NotificationRule.Custom) {
                    fields.weekdays.value = notification.weekdays
                }
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun saveNotificationRule() {
        if (!fields.validateAll()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = if (ruleId == null) {
                notificationRuleRepository.addNotificationRule(
                    fields.toNotificationRuleCreate()
                )
            } else {
                notificationRuleRepository.updateNotificationRule(
                    ruleId, fields.toNotificationRuleUpdate()
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isOperationSuccessful = true) }
            }.onFailure { exception ->
                Log.d("RoutineNotificationFormVM", "Error saving notification rule", exception)
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
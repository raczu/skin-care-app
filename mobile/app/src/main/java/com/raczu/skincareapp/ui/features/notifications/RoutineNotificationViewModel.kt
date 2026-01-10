package com.raczu.skincareapp.ui.features.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.local.entities.RoutineNotification
import com.raczu.skincareapp.utils.enums.RoutineType
import com.raczu.skincareapp.data.repositories.RoutineNotificationRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalTime

data class RoutineNotificationUiState(
    val morningRoutineTime: LocalTime? = null,
    val nightRoutineTime: LocalTime? = null
)

class RoutineNotificationViewModel(
    private val routineNotificationRepository: RoutineNotificationRepository
) : ViewModel() {
    var routineNotificationUiState by mutableStateOf(RoutineNotificationUiState())
        private set

    init {
        viewModelScope.launch {
            combine(
                routineNotificationRepository.getNotification(RoutineType.MORNING),
                routineNotificationRepository.getNotification(RoutineType.NIGHT)
            ) { morningRoutine, nightRoutine ->
                RoutineNotificationUiState(
                    morningRoutineTime = morningRoutine?.time,
                    nightRoutineTime = nightRoutine?.time
                )
            }.collect { routineNotificationUiState = it }
        }
    }

    fun insert(type: RoutineType) {
        val time = when (type) {
            RoutineType.MORNING -> routineNotificationUiState.morningRoutineTime
            RoutineType.NIGHT -> routineNotificationUiState.nightRoutineTime
        }

        if (time != null) {
            viewModelScope.launch {
                routineNotificationRepository.insert(
                    RoutineNotification(type = type, time = time)
                )
            }
        }
    }

    fun updateUiState(type: RoutineType, time: LocalTime) {
        routineNotificationUiState = when (type) {
            RoutineType.MORNING -> routineNotificationUiState.copy(
                morningRoutineTime = time
            )
            RoutineType.NIGHT -> routineNotificationUiState.copy(
                nightRoutineTime = time
            )
        }
    }
}
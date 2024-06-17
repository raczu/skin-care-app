package com.raczu.skincareapp.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.entities.RoutineNotification
import com.raczu.skincareapp.enums.RoutineType
import com.raczu.skincareapp.repositories.RoutineNotificationRepository
import kotlinx.coroutines.flow.firstOrNull
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
            routineNotificationRepository
                .getNotification(RoutineType.MORNING).collect {
                    routineNotificationUiState = routineNotificationUiState.copy(
                        morningRoutineTime = it?.time
                )
            }

            routineNotificationRepository
                .getNotification(RoutineType.NIGHT).collect {
                    routineNotificationUiState = routineNotificationUiState.copy(
                        nightRoutineTime = it?.time
                )
            }
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
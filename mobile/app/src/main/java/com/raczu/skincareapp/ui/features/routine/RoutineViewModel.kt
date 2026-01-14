package com.raczu.skincareapp.ui.features.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.repository.RoutineRepository
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class RoutineUiState(
    val isLoading: Boolean = false,
    val routines: List<Routine> = emptyList(),
    val error: String? = null,
    val selectedDate: LocalDate = LocalDate.now()
)

class RoutineViewModel(
    private val routineRepository: RoutineRepository
) : ViewModel() {
    companion object {
        private const val ONE_DAY_LIMIT = 50
    }

    private val _uiState = MutableStateFlow(RoutineUiState())
    val uiState: StateFlow<RoutineUiState> = _uiState.asStateFlow()

    init {
        loadRoutinesForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        if (date == _uiState.value.selectedDate) return
        loadRoutinesForDate(date)
    }

    fun loadRoutinesForDate(date: LocalDate = _uiState.value.selectedDate) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    selectedDate = date,
                    routines = emptyList()
                )
            }

            val result = routineRepository.getRoutines(
                limit = ONE_DAY_LIMIT,
                offset = 0,
                performedAfter = date.atStartOfDay(),
                performedBefore = date.atTime(LocalTime.MAX)
            )

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(isLoading = false, routines = response.routines)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            val result = routineRepository.deleteRoutine(routineId)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        routines = it.routines.filterNot { routine -> routine.id == routineId }
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
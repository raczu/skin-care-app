package com.raczu.skincareapp.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.repositories.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RoutineUiState(
    val routines: List<Routine> = listOf(),
    val timestamp: Long = System.currentTimeMillis()
)

class RoutineViewModel(private val routineRepository: RoutineRepository) : ViewModel() {
    private val _routineUiState = MutableStateFlow(RoutineUiState())
    val routineUiState: StateFlow<RoutineUiState> get() = _routineUiState

    init {
        updateUiState(_routineUiState.value.timestamp)
    }

    fun updateUiState(timestamp: Long) {
        viewModelScope.launch {
            routineRepository.getGivenDateRoutines(timestamp).collect {
                _routineUiState.value = RoutineUiState(it, timestamp)
            }
        }
    }

    fun delete(routine: Routine) {
        viewModelScope.launch {
            routineRepository.delete(routine)
        }
    }
}
package com.raczu.skincareapp.ui.features.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.repository.RoutineRepository
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoutineDetailsUiState(
    val isLoading: Boolean = true,
    val routine: Routine? = null,
    val error: String? = null
)

class RoutineDetailsViewModel(
    private val routineRepository: RoutineRepository,
    private val routineId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadRoutineDetails()
    }

    fun loadRoutineDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = routineRepository.getRoutineDetails(routineId)
            result.onSuccess { routine ->
                _uiState.update {
                    it.copy(isLoading = false, routine = routine, error = null)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
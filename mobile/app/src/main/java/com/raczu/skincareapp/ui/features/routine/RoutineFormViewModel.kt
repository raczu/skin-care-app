package com.raczu.skincareapp.ui.features.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.routine.RoutineCreate
import com.raczu.skincareapp.data.domain.models.routine.RoutineType
import com.raczu.skincareapp.data.domain.models.routine.RoutineUpdate
import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.rules.NoOpValidator
import com.raczu.skincareapp.data.domain.validation.rules.NotEmptyCollectionValidator
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.data.repository.RoutineRepository
import com.raczu.skincareapp.ui.common.FormFieldState
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.collections.emptySet


data class RoutineFormUiState(
    val isLoading: Boolean = false,
    val isOperationSuccessful: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val availableProducts: List<Product> = emptyList()
)

class RoutineFormViewModel(
    private val routineRepository: RoutineRepository,
    private val productRepository: ProductRepository,
    private val routineId: String? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineFormUiState(isEditMode = routineId != null))
    val uiState: StateFlow<RoutineFormUiState> = _uiState.asStateFlow()

    companion object {
        // TODO: Implement search-based loading (requires changes in backend).
        private const val PRODUCT_LIMIT = 100
    }

    class RoutineFormFields(onChanged: () -> Unit) {
        val date = FormFieldState(
            initialValue = LocalDate.now(),
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        val time = FormFieldState(
            initialValue = LocalTime.now(),
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        val type = FormFieldState(
            initialValue = RoutineType.MORNING,
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        val selectedProductIds = FormFieldState(
            initialValue = emptySet<String>(),
            validator = NotEmptyCollectionValidator("Select at least one product"),
            onValueChangeCallback = onChanged
        )
        val notes = FormFieldState(
            initialValue = "",
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )

        fun validateAll() = listOf(
            date,
            time,
            type,
            selectedProductIds,
            notes
        ).all { it.validate() }
    }

    private val onFieldChanged = {
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
    val fields = RoutineFormFields(onChanged = onFieldChanged)

    init {
        initializeForm()
    }

    private fun initializeForm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loadProductsSuspend()

            if (routineId != null) {
                loadRoutineDetails(routineId)
            } else {
                setupDefaultValuesForNewRoutine()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun loadProductsSuspend() {
        val result = productRepository.getProducts(limit = PRODUCT_LIMIT)
        result.onSuccess { response ->
            _uiState.update {
                it.copy(
                    availableProducts = response.products,
                    isLoading = false
                )
            }
        }.onFailure { exception ->
            _uiState.update {
                it.copy(isLoading = false, error = exception.toUiErrorMessage())
            }
        }
    }

    private fun loadRoutineDetails(routineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = routineRepository.getRoutineDetails(routineId)

            result.onSuccess { routine ->
                fields.date.value = routine.performedAt.toLocalDate()
                fields.time.value = routine.performedAt.toLocalTime()
                fields.type.value = routine.type
                fields.selectedProductIds.value = routine.products.map { it.id }.toSet()
                _uiState .update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    private fun setupDefaultValuesForNewRoutine() {
        val now = LocalTime.now()
        val smartType = when (now.hour) {
            in 5..11 -> RoutineType.MORNING
            in 18..23 -> RoutineType.NIGHT
            else -> RoutineType.DAILY
        }
        fields.type.value = smartType
    }

    fun toggleProductSelection(productId: String) {
        val currentIds = fields.selectedProductIds.value.toMutableSet()
        if (currentIds.contains(productId)) currentIds.remove(productId)
        else currentIds.add(productId)
        fields.selectedProductIds.value = currentIds.toSet()
    }

    fun saveRoutine() {
        if (!fields.validateAll()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val dateTime = LocalDateTime.of(fields.date.value, fields.time.value)
            val productIds = fields.selectedProductIds.value.toList()
            val notes = fields.notes.value.ifBlank { null }
            val type = fields.type.value

            val result = if (routineId == null) {
                routineRepository.addRoutine(
                    RoutineCreate(type, notes, dateTime, productIds)
                )
            } else {
                routineRepository.updateRoutine(
                    routineId,
                    RoutineUpdate(type, notes, dateTime, productIds)
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isOperationSuccessful = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
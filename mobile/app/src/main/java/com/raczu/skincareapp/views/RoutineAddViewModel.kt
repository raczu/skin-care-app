package com.raczu.skincareapp.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.entities.RoutineWithProducts
import com.raczu.skincareapp.enums.RoutineType
import com.raczu.skincareapp.repositories.ProductRepository
import com.raczu.skincareapp.repositories.RoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RoutineFormData(
    val type: RoutineType = RoutineType.MORNING,
    val products: List<Product> = listOf()
)

data class RoutineAddUiState(
    val formData: RoutineFormData = RoutineFormData(),
    val isEntryValid: Boolean = false
)

class RoutineAddViewModel(
    private val routineRepository: RoutineRepository,
    productRepository: ProductRepository
) : ViewModel() {
    var routineAddUiState by mutableStateOf(RoutineAddUiState())
        private set

    val products: StateFlow<List<Product>> =
        productRepository.getAllProducts().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT),
                initialValue = listOf()
            )

    fun updateUiState(formData: RoutineFormData) {
        routineAddUiState = RoutineAddUiState(
            formData = formData,
            isEntryValid = formData.products.isNotEmpty()
        )
    }

    fun insert() {
        if (routineAddUiState.isEntryValid) {
            viewModelScope.launch {
                val routineWithProducts = routineAddUiState
                    .formData
                    .toRoutineWithProducts()
                routineRepository.insertWithProducts(
                    routineWithProducts.routine, routineWithProducts.products
                )
            }
        }
    }

    companion object {
        private const val TIMEOUT = 5_000L
    }
}

fun RoutineFormData.toRoutineWithProducts(): RoutineWithProducts {
    return RoutineWithProducts(
        routine = Routine(type = type),
        products = products
    )
}
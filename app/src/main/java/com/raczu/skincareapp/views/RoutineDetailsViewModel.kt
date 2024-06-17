package com.raczu.skincareapp.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.entities.RoutineWithProducts
import com.raczu.skincareapp.enums.RoutineType
import com.raczu.skincareapp.navigation.TopBarScreen
import com.raczu.skincareapp.repositories.RoutineRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class RoutineDetailsUiState(
    val routine: Routine = Routine(
        type = RoutineType.MORNING,
    ),
    val products: List<Product> = listOf()
)

class RoutineDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val routineRepository: RoutineRepository
) : ViewModel() {
    var routineDetailsUiState by mutableStateOf(RoutineDetailsUiState())
        private set

    private val routineId: Int = checkNotNull(
        savedStateHandle[TopBarScreen.RoutineDetails.args]
    )

    init {
        viewModelScope.launch {
            routineDetailsUiState = routineRepository
                .getRoutineWithProducts(routineId)
                .filterNotNull()
                .first()
                .toProductEditUiState()
        }
    }

}

fun RoutineWithProducts.toProductEditUiState(): RoutineDetailsUiState {
    return RoutineDetailsUiState(
        routine = routine,
        products = products
    )
}
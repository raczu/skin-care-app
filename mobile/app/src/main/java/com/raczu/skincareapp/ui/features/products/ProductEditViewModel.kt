package com.raczu.skincareapp.ui.features.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.ui.components.ProductFormData
import com.raczu.skincareapp.data.local.entities.Product
import com.raczu.skincareapp.ui.navigation.TopBarScreen
import com.raczu.skincareapp.data.repositories.ProductRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProductEditUiState(
    val productId: Int = 0,
    val formData: ProductFormData = ProductFormData(),
    val isEntryValid: Boolean = false
)

class ProductEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {
    var productEditUiState by mutableStateOf(ProductEditUiState())
        private set

    private val productId: Int = checkNotNull(
        savedStateHandle[TopBarScreen.ProductEdit.args]
    )

    init {
        viewModelScope.launch {
            productEditUiState = productRepository
                .getProduct(productId)
                .filterNotNull()
                .first()
                .toProductEditUiState()
        }
    }

    fun updateUiState(formData: ProductFormData) {
        productEditUiState = ProductEditUiState(
            productId = productId,
            formData = formData,
            isEntryValid = formData.name.isNotBlank()
        )
    }

    fun update() {
        if (productEditUiState.isEntryValid) {
            viewModelScope.launch {
                productRepository.update(productEditUiState.toEntity())
            }
        }
    }
}


fun Product.toProductEditUiState(): ProductEditUiState {
    return ProductEditUiState(
        productId = productId,
        formData = ProductFormData(
            name = name,
            description = description,
            purpose = purpose
        ),
        isEntryValid = true
    )
}

fun ProductEditUiState.toEntity(): Product {
    return Product(
        productId = productId,
        name = formData.name,
        description = formData.description,
        purpose = formData.purpose
    )
}
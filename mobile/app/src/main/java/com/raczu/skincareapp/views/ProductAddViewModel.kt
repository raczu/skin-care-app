package com.raczu.skincareapp.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.components.ProductFormData
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.repositories.ProductRepository
import kotlinx.coroutines.launch


data class ProductAddUiState(
    val formData: ProductFormData = ProductFormData(),
    val isEntryValid: Boolean = false
)

class ProductAddViewModel(private val productRepository: ProductRepository): ViewModel() {
    var productAddUiState by mutableStateOf(ProductAddUiState())
        private set

    fun updateUiState(formData: ProductFormData) {
        productAddUiState = ProductAddUiState(
            formData = formData,
            isEntryValid = formData.name.isNotBlank()
        )
    }

    fun insert() {
        if (productAddUiState.isEntryValid) {
            viewModelScope.launch {
                productRepository.insert(productAddUiState.toEntity())
            }
        }
    }
}

fun ProductAddUiState.toEntity(): Product {
    return Product(
        name = formData.name,
        description = formData.description,
        purpose = formData.purpose
    )
}
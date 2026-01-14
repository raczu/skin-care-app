package com.raczu.skincareapp.ui.features.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.product.ProductCreate
import com.raczu.skincareapp.data.domain.models.product.ProductUpdate
import com.raczu.skincareapp.data.domain.validation.rules.NoOpValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.ui.common.FormFieldState
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductFormUiState(
    val isLoading: Boolean = false,
    val isOperationSuccessful: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false
)

class ProductFormViewModel(
    private val productRepository: ProductRepository,
    private val productId: String? = null
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductFormUiState(isEditMode = productId != null))
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    class ProductFormFields(onChanged: () -> Unit) {
        val name = FormFieldState(
            initialValue = "",
            validator = RequiredValidator("Name is required"),
            onValueChangeCallback = onChanged
        )
        var brand = FormFieldState(
            initialValue = "",
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        var purpose = FormFieldState(
            initialValue = "",
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )
        var description = FormFieldState(
            initialValue = "",
            validator = NoOpValidator(),
            onValueChangeCallback = onChanged
        )

        fun validateAll() = name.validate()
    }

    private val onFieldChanged = {
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }
    val fields = ProductFormFields(onChanged = onFieldChanged)

    init {
        if (productId != null) {
            loadProductDetails(productId)
        }
    }

    private fun loadProductDetails(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = productRepository.getProductDetails(productId)

            result.onSuccess { product ->
                fields.name.value = product.name
                fields.brand.value = product.brand ?: ""
                fields.purpose.value = product.purpose ?: ""
                fields.description.value = product.description ?: ""
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun saveProduct() {
        if (!fields.validateAll()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val name = fields.name.value
            val brand = fields.brand.value.ifBlank { null }
            val purpose = fields.purpose.value.ifBlank { null }
            val desc = fields.description.value.ifBlank { null }

            val result = if (productId == null) {
                productRepository.addProduct(
                    ProductCreate(name, brand, purpose, desc)
                )
            } else {
                productRepository.updateProduct(
                    productId, ProductUpdate(name, brand, purpose, desc)
                )
            }

            result.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, isOperationSuccessful = true)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
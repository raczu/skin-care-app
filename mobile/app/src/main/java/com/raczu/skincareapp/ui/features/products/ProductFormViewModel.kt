package com.raczu.skincareapp.ui.features.products

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.product.ProductCreate
import com.raczu.skincareapp.data.domain.models.product.ProductUpdate
import com.raczu.skincareapp.data.domain.validation.rules.NoOpValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.ui.common.TextFieldState
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
    var uiState by mutableStateOf(ProductFormUiState(isEditMode = productId != null))
        private set

    class ProductFormFields(onChanged: () -> Unit) {
        val name = TextFieldState(
            validator = RequiredValidator("Name is required"),
            onValueChangeCallback = onChanged
        )
        var brand = TextFieldState(
            validator = NoOpValidator,
            onValueChangeCallback = onChanged
        )
        var purpose = TextFieldState(
            validator = NoOpValidator,
            onValueChangeCallback = onChanged
        )
        var description = TextFieldState(
            validator = NoOpValidator,
            onValueChangeCallback = onChanged
        )

        fun validateAll() = name.validate()
    }

    private val onFieldChanged = {
        if (uiState.error != null) {
            uiState = uiState.copy(error = null)
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
            uiState = uiState.copy(isLoading = true, error = null)
            val result = productRepository.getProductDetails(productId)

            result.onSuccess { product ->
                fields.name.text = product.name
                fields.brand.text = product.brand ?: ""
                fields.purpose.text = product.purpose ?: ""
                fields.description.text = product.description ?: ""
                uiState = uiState.copy(isLoading = false)
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }

                uiState = uiState.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    fun saveProduct() {
        if (!fields.validateAll()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val name = fields.name.text
            val brand = fields.brand.text.ifBlank { null }
            val purpose = fields.purpose.text.ifBlank { null }
            val desc = fields.description.text.ifBlank { null }

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
                uiState = uiState.copy(
                    isLoading = false,
                    isOperationSuccessful = true
                )
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }

                Log.e("ProductFormViewModel", "Error saving product", exception)

                uiState = uiState.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
}
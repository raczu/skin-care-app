package com.raczu.skincareapp.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.entities.Product
import com.raczu.skincareapp.repositories.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = listOf()
)


class ProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
    val productsUiState: StateFlow<ProductsUiState> =
        productRepository.getAllProducts().map { ProductsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT),
                initialValue = ProductsUiState()
            )

    fun delete(product: Product) {
        viewModelScope.launch {
            productRepository.delete(product)
        }
    }

    companion object {
        private const val TIMEOUT = 5_000L
    }
}
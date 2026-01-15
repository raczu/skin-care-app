package com.raczu.skincareapp.ui.features.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = true
)

private data class ProductsLoadState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
)

class ProductsViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 15
    }

    private val _loadState = MutableStateFlow(ProductsLoadState())
    val uiState: StateFlow<ProductsUiState> = combine(
        productRepository.products,
        _loadState
    ) { products, loadState ->
        ProductsUiState(
            isLoading = loadState.isLoading,
            products = products,
            error = loadState.error,
            hasMore = loadState.hasMore
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ProductsUiState(isLoading = true)
    )

    init {
        loadMoreProducts()
    }

    fun loadMoreProducts() {
        if (_loadState.value.isLoading || !_loadState.value.hasMore) return

        viewModelScope.launch {
            _loadState.update { it.copy(isLoading = true, error = null) }

            val currentOffset = productRepository.products.value.size
            val result = productRepository.getProducts(
                limit = PAGE_SIZE,
                offset = currentOffset
            )

            result.onSuccess { response ->
                _loadState.update {
                    it.copy(isLoading = false, hasMore = response.hasMore)
                }
            }.onFailure { exception ->
                _loadState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = productRepository.deleteProduct(productId)
            result.onFailure { exception ->
                _loadState.update { it.copy(isLoading = false, error = exception.toUiErrorMessage()) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _loadState.update { it.copy(isLoading = true, error = null) }
            val result = productRepository.getProducts(limit = PAGE_SIZE, offset = 0)
            result.onSuccess { response ->
                _loadState.update {
                    it.copy(isLoading = false, hasMore = response.hasMore)
                }
            }.onFailure { exception ->
                _loadState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
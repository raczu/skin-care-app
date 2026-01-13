package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.product.ProductCreate
import com.raczu.skincareapp.data.domain.models.product.ProductUpdate
import com.raczu.skincareapp.data.domain.models.product.ProductsPage
import com.raczu.skincareapp.data.mappers.toDomain
import com.raczu.skincareapp.data.mappers.toRequest
import com.raczu.skincareapp.data.remote.api.ProductApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import com.raczu.skincareapp.data.remote.dto.product.ProductCreateRequest
import com.raczu.skincareapp.data.remote.dto.product.ProductUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteProductRepository(
    private val productApiService: ProductApiService
) : ProductRepository {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    override val products: StateFlow<List<Product>> = _products.asStateFlow()

    override suspend fun addProduct(product: ProductCreate): Result<Product> {
        val request = product.toRequest()
        val result = safeApiCall { productApiService.addProduct(request) }

        return result.map { response ->
            val newProduct = response.toDomain()
            _products.update { curr ->
                listOf(newProduct) + curr
            }
            newProduct
        }
    }

    override suspend fun getProducts(limit: Int, offset: Int): Result<ProductsPage> {
        val result = safeApiCall { productApiService.getProducts(limit, offset) }

        return result.map { response ->
            val domainProducts = response.items.map { it.toDomain() }
            _products.update { curr ->
                if (offset == 0) domainProducts
                else (curr + domainProducts).distinctBy { it.id }
            }

            ProductsPage(
                products = domainProducts,
                hasMore = (response.pagination.offset + response.pagination.limit) < response.meta.total
            )
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        val result = safeApiCall { productApiService.deleteProduct(productId) }
        if (result.isSuccess) {
            _products.update { curr ->
                curr.filterNot { it.id == productId }
            }
        }
        return result
    }

    override suspend fun updateProduct(productId: String, update: ProductUpdate): Result<Product> {
        val request = update.toRequest()
        val result = safeApiCall { productApiService.updateProduct(productId, request) }

        return result.map { response ->
            val updatedProduct = response.toDomain()
            _products.update { curr ->
                curr.map { if (it.id == productId) updatedProduct else it }
            }
            updatedProduct
        }
    }

    override suspend fun getProductDetails(productId: String): Result<Product> {
        val result = safeApiCall { productApiService.getProductDetails(productId) }
        return result.map { response ->
            val details = response.toDomain()
            _products.update { curr ->
                curr.map { if(it.id == productId) details else it }
            }
            details
        }
    }

    override fun clearData() {
        _products.update { emptyList() }
    }
}
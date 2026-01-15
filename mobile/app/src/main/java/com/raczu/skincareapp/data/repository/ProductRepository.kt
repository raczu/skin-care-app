package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.product.Product
import com.raczu.skincareapp.data.domain.models.product.ProductCreate
import com.raczu.skincareapp.data.domain.models.product.ProductUpdate
import com.raczu.skincareapp.data.domain.models.product.ProductsPage
import kotlinx.coroutines.flow.StateFlow

interface ProductRepository : CleanableRepository {
    val products: StateFlow<List<Product>>

    suspend fun getProducts(limit: Int = 15, offset: Int = 0): Result<ProductsPage>
    suspend fun getProductDetails(productId: String): Result<Product>
    suspend fun addProduct(product: ProductCreate): Result<Product>
    suspend fun updateProduct(productId: String, update: ProductUpdate): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
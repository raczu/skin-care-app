package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.local.daos.ProductDao
import com.raczu.skincareapp.data.local.entities.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    suspend fun insert(product: Product) = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)

    fun getProduct(id: Int): Flow<Product?> = productDao.getProduct(id)

    fun getProductsWithCursor(limit: Int = 0, offset: Int = 15): Flow<List<Product>> {
        return productDao.getProductsWithCursor(limit, offset)
    }

    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
}
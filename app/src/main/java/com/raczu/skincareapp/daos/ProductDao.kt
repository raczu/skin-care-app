package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.raczu.skincareapp.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM product WHERE product_id = :id")
    fun getProduct(id: Int): Flow<Product?>

    @Query("SELECT * FROM product LIMIT :limit OFFSET :offset")
    fun getProductsWithCursor(limit: Int = 0, offset: Int = 15): Flow<List<Product>>

    @Query("SELECT * FROM product")
    fun getAllProducts(): Flow<List<Product>>
}
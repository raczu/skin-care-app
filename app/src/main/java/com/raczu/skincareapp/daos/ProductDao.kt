package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.raczu.skincareapp.entities.Product

@Dao
interface ProductDao {
    @Insert
    fun insert(product: Product)

    @Update
    fun update(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("SELECT * FROM product WHERE product_id = :id")
    fun getProduct(id: Int): Product

    @Query("SELECT * FROM product LIMIT :limit OFFSET :offset")
    fun getProductsWithCursor(limit: Int = 0, offset: Int = 15): List<Product>
}
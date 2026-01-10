package com.raczu.skincareapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("product_id")
    val productId: Int = 0,
    val name: String,
    val description: String? = null,
    val purpose: String? = null,
)
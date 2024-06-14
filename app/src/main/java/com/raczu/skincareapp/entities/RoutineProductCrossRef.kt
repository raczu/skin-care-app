package com.raczu.skincareapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "routine_product",
    primaryKeys = ["routine_id", "product_id"],
    indices = [Index("routine_id"), Index("product_id")]
)
data class RoutineProductCrossRef(
    @ColumnInfo("routine_id")
    val routineId: Int,
    @ColumnInfo("product_id")
    val productId: Int
)

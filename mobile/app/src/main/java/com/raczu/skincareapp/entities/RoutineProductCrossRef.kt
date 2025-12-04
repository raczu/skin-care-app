package com.raczu.skincareapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "routine_product",
    primaryKeys = ["routine_id", "product_id"],
    indices = [Index("routine_id"), Index("product_id")],
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )

    ]
)
data class RoutineProductCrossRef(
    @ColumnInfo("routine_id")
    val routineId: Int,
    @ColumnInfo("product_id")
    val productId: Int
)

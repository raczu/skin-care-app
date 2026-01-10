package com.raczu.skincareapp.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoutineWithProducts(
    @Embedded
    val routine: Routine,
    @Relation(
        parentColumn = "routine_id",
        entityColumn = "product_id",
        associateBy = Junction(RoutineProductCrossRef::class)
    )
    val products: List<Product>
)
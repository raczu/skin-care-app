package com.raczu.skincareapp.data.domain.models.routine

import com.raczu.skincareapp.data.domain.models.product.Product
import java.time.LocalDateTime

data class Routine(
    val id: String,
    val type: RoutineType,
    val notes: String? = null,
    val performedAt: LocalDateTime,
    val products: List<Product>
)

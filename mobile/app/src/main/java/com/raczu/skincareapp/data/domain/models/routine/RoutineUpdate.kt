package com.raczu.skincareapp.data.domain.models.routine

import java.time.LocalDateTime

data class RoutineUpdate(
    val type: RoutineType? = null,
    val notes: String? = null,
    val performedAt: LocalDateTime? = null,
    val productIds: List<String>? = null
)

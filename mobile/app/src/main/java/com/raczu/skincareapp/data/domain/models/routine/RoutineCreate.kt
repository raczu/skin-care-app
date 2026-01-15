package com.raczu.skincareapp.data.domain.models.routine

import java.time.LocalDateTime

data class RoutineCreate(
    val type: RoutineType? = null,
    val notes: String? = null,
    val performedAt: LocalDateTime = LocalDateTime.now(),
    val productIds: List<String>
)

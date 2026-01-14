package com.raczu.skincareapp.data.remote.dto.routine

import com.google.gson.annotations.SerializedName
import com.raczu.skincareapp.data.remote.ExplicitNull

data class RoutineUpdateRequest(
    val type: String? = null,
    val notes: ExplicitNull<String>? = null,

    @SerializedName("performed_at")
    val performedAt: String? = null,
    val productIds: List<String>? = null
)

package com.raczu.skincareapp.data.remote.dto.routine

import com.google.gson.annotations.SerializedName

data class RoutineUpdateRequest(
    val type: String? = null,
    val notes: String? = null,

    @SerializedName("performed_at")
    val performedAt: String? = null,
    val productIds: List<String>? = null
)

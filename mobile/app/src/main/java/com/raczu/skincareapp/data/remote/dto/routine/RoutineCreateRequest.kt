package com.raczu.skincareapp.data.remote.dto.routine

import com.google.gson.annotations.SerializedName

data class RoutineCreateRequest(
    val type: String? = null,
    val notes: String? = null,

    @SerializedName("performed_at")
    val performedAt: String,

    @SerializedName("product_ids")
    val productIds: List<String>
)

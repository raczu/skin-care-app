package com.raczu.skincareapp.data.remote.dto.routine

import com.google.gson.annotations.SerializedName
import com.raczu.skincareapp.data.remote.dto.product.ProductResponse

data class RoutineResponse(
    val id: String,
    val type: String,
    val notes: String?,

    @SerializedName("performed_at")
    val performedAt: String,
    val products: List<ProductResponse>
)

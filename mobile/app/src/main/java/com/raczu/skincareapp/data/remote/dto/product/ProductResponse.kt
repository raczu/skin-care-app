package com.raczu.skincareapp.data.remote.dto.product

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class ProductResponse(
    val id: String,
    val name: String,
    val brand: String? = null,
    val purpose: String? = null,
    val description: String? = null,

    @SerializedName("created_at")
    val createdAt: Instant,

    @SerializedName("updated_at")
    val updatedAt: Instant
)

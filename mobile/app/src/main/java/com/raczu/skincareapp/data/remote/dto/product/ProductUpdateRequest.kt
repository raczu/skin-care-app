package com.raczu.skincareapp.data.remote.dto.product

data class ProductUpdateRequest(
    val name: String? = null,
    val brand: String? = null,
    val purpose: String? = null,
    val description: String? = null
)

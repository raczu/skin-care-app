package com.raczu.skincareapp.data.remote.dto.product

data class ProductCreateRequest(
    val name: String,
    val brand: String? = null,
    val purpose: String? = null,
    val description: String? = null
)

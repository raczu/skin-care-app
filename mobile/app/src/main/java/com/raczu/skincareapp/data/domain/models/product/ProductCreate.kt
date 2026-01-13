package com.raczu.skincareapp.data.domain.models.product

data class ProductCreate(
    val name: String,
    val brand: String? = null,
    val purpose: String? = null,
    val description: String? = null
)

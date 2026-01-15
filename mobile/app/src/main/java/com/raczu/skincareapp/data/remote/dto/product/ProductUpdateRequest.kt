package com.raczu.skincareapp.data.remote.dto.product

import com.raczu.skincareapp.data.remote.ExplicitNull

data class ProductUpdateRequest(
    val name: String? = null,
    val brand: ExplicitNull<String>? = null,
    val purpose: ExplicitNull<String>? = null,
    val description: ExplicitNull<String>? = null
)

package com.raczu.skincareapp.data.domain.models.product

data class ProductsPage(
    val products: List<Product>,
    val hasMore: Boolean
)

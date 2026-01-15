package com.raczu.skincareapp.data.remote.dto

data class GenericMultipleItemsResponse<T>(
    val items: List<T>
)

data class PagedResponse<T>(
    val items: List<T>,
    val meta: MetaResponse,
    val pagination: PaginationResponse
)

data class MetaResponse(
    val total: Int,
    val count: Int
)

data class PaginationResponse(
    val limit: Int,
    val offset: Int
)
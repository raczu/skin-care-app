package com.raczu.skincareapp.data.remote.dto

data class ProblemDetails(
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String,
    val errors: List<ValidationError>? = null
)

data class ValidationError(
    val field: String,
    val message: String
)
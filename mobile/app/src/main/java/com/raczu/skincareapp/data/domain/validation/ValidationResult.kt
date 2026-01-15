package com.raczu.skincareapp.data.domain.validation

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val error: String) : ValidationResult()
}

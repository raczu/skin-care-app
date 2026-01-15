package com.raczu.skincareapp.data.domain.validation

fun interface FieldValidator<T> {
    fun validate(value: T): ValidationResult
}

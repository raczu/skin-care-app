package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class NotEmptyCollectionValidator<T : Iterable<*>>(
    private val message: String = "Selection cannot be empty"
) : FieldValidator<T> {
    override fun validate(value: T): ValidationResult {
        val isNotEmpty = when (value) {
            is Collection<*> -> value.isNotEmpty()
            else -> value.any()
        }
        return if (isNotEmpty) ValidationResult.Valid
        else ValidationResult.Invalid(message)
    }
}

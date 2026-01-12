package com.raczu.skincareapp.data.domain.validation

class CompositeValidator<T>(
    private vararg val validators: FieldValidator<T>
) : FieldValidator<T> {
    override fun validate(value: T): ValidationResult {
        validators.forEach { v ->
            val result = v.validate(value)
            if (result is ValidationResult.Invalid) return result
        }
        return ValidationResult.Valid
    }
}
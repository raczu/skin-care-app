package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class NoOpValidator<T> : FieldValidator<T> {
    override fun validate(value: T): ValidationResult = ValidationResult.Valid
}
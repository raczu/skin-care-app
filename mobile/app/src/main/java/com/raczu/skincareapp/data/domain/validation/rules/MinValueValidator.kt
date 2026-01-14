package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class MinValueValidator(private val minValue: Int) : FieldValidator<Int> {
    override fun validate(value: Int): ValidationResult {
        return if (value >= minValue) ValidationResult.Valid
        else ValidationResult.Invalid("Value must be at least 1")
    }
}
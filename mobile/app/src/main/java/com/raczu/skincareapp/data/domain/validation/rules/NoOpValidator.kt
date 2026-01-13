package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

object NoOpValidator : FieldValidator<String> {
    override fun validate(value: String): ValidationResult = ValidationResult.Valid
}
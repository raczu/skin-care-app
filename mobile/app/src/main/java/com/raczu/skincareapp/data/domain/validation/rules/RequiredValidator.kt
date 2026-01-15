package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class RequiredValidator(val message: String = "Field is required"): FieldValidator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value.isBlank()) ValidationResult.Invalid(message)
        else ValidationResult.Valid
    }
}
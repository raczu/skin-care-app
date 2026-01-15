package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class MatchValidator(
    private val targetTextProvider: () -> String,
    private val message: String = "Fields do not match"
) : FieldValidator<String> {
    override fun validate(value: String): ValidationResult {
        return if (value == targetTextProvider()) ValidationResult.Valid
        else ValidationResult.Invalid(message)
    }
}
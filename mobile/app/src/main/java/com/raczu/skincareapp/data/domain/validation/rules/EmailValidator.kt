package com.raczu.skincareapp.data.domain.validation.rules

import android.util.Patterns
import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

object EmailValidator : FieldValidator<String> {
    override fun validate(value: String): ValidationResult {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(value).matches()
        return if (isValid) ValidationResult.Valid
        else ValidationResult.Invalid("Invalid email address format")
    }
}
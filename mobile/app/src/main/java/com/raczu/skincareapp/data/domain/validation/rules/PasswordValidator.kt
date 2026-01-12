package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

object PasswordValidator : FieldValidator<String> {
    private const val PASSWORD_PATTERN =
        "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$"
    private val regex = Regex(PASSWORD_PATTERN)

    override fun validate(value: String): ValidationResult {
        return if (value.matches(regex)) ValidationResult.Valid
        else ValidationResult.Invalid(
            "Password must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one lowercase letter, " +
                    "one digit, and one special character."
        )
    }
}
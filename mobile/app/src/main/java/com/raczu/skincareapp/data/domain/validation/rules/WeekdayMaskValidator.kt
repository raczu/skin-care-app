package com.raczu.skincareapp.data.domain.validation.rules

import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class WeekdayMaskValidator : FieldValidator<List<Int>> {
    override fun validate(value: List<Int>): ValidationResult {
        if (value.size != 7) {
            return ValidationResult.Invalid("Weekday mask must have exactly 7 elements")
        }
        val hasInvalidValues = value.any { it != 0 && it != 1 }
        if (hasInvalidValues) {
            return ValidationResult.Invalid("Weekday mask can only contain 0s and 1s")
        }

        val isAtLeastOneSelected = value.any { it == 1 }
        return if (isAtLeastOneSelected) ValidationResult.Valid
        else ValidationResult.Invalid("At least one day must be selected")
    }
}

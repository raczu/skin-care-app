package com.raczu.skincareapp.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class FormFieldState<T>(
    initialValue: T,
    val validator: FieldValidator<T>,
    private val onValueChangeCallback: () -> Unit = {}
) {
    var value by mutableStateOf(initialValue)
    var error by mutableStateOf<String?>(null)

    fun onValueChange(newValue: T) {
        value = newValue
        if (error != null) error = null
        onValueChangeCallback()
    }

    fun validate(): Boolean {
        return when (val result = validator.validate(value)) {
            is ValidationResult.Valid -> {
                error = null
                true
            }
            is ValidationResult.Invalid -> {
                error = result.error
                false
            }
        }
    }
}
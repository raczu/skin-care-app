package com.raczu.skincareapp.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.raczu.skincareapp.data.domain.validation.FieldValidator
import com.raczu.skincareapp.data.domain.validation.ValidationResult

class TextFieldState(
    val validator: FieldValidator<String>,
    private val onValueChangeCallback: () -> Unit = {}
) {
    var text by mutableStateOf("")
    var error by mutableStateOf<String?>(null)

    fun onValueChange(value: String) {
        text = value
        if (error != null) error = null
        onValueChangeCallback()
    }

    fun validate(): Boolean {
        return when (val result = validator.validate(text)) {
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
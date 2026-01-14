package com.raczu.skincareapp.ui.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.validation.CompositeValidator
import com.raczu.skincareapp.data.domain.validation.rules.EmailValidator
import com.raczu.skincareapp.data.domain.validation.rules.MatchValidator
import com.raczu.skincareapp.data.domain.validation.rules.PasswordValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.repository.UserRepository
import com.raczu.skincareapp.ui.common.FormFieldState
import com.raczu.skincareapp.ui.common.TextFieldState
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    class RegisterFields(onChanged: () -> Unit) {
        val email = FormFieldState(
            initialValue = "",
            validator = CompositeValidator(
                RequiredValidator("Email is required"),
                EmailValidator
            ),
            onValueChangeCallback = onChanged
        )
        val name = FormFieldState(
            initialValue = "",
            validator = RequiredValidator("Name is required"),
            onValueChangeCallback = onChanged
        )
        val surname = FormFieldState(
            initialValue = "",
            validator = RequiredValidator("Surname is required"),
            onValueChangeCallback = onChanged
        )
        val username = FormFieldState(
            initialValue = "",
            validator = RequiredValidator("Username is required"),
            onValueChangeCallback = onChanged
        )
        val password = FormFieldState(
            initialValue = "",
            validator = CompositeValidator(
                RequiredValidator("Password is required"),
                PasswordValidator
            ),
            onValueChangeCallback = onChanged
        )
        val confirmPassword = FormFieldState(
            initialValue = "",
            validator = CompositeValidator(
                RequiredValidator("Please confirm your password"),
                MatchValidator(
                    targetTextProvider = { password.value },
                    "Passwords do not match"
                )
            ),
            onValueChangeCallback = onChanged
        )

        fun validateAll() = listOf(
            email,
            name,
            surname,
            username,
            password,
            confirmPassword
        ).map { it.validate() }.all { it }
    }

    private val onFieldChanged = {
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }
    val fields = RegisterFields(onChanged = onFieldChanged)

    fun register() {
        if (!fields.validateAll()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = UserRegistration(
                email = fields.email.value,
                name = fields.name.value,
                surname = fields.surname.value,
                username = fields.username.value,
                password = fields.password.value
            )

            val result = userRepository.register(user)
            result.onSuccess { _ ->
                _uiState.update {
                    it.copy(isLoading = false, isRegistrationSuccessful = true)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }

    }
}
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
        val email = TextFieldState(
            validator = CompositeValidator(
                RequiredValidator("Email is required"),
                EmailValidator
            ),
            onValueChangeCallback = onChanged
        )
        val name = TextFieldState(
            validator = RequiredValidator("Name is required"),
            onValueChangeCallback = onChanged
        )
        val surname = TextFieldState(
            validator = RequiredValidator("Surname is required"),
            onValueChangeCallback = onChanged
        )
        val username = TextFieldState(
            validator = RequiredValidator("Username is required"),
            onValueChangeCallback = onChanged
        )
        val password = TextFieldState(
            validator = CompositeValidator(
                RequiredValidator("Password is required"),
                PasswordValidator
            ),
            onValueChangeCallback = onChanged
        )
        val confirmPassword = TextFieldState(
            validator = CompositeValidator(
                RequiredValidator("Please confirm your password"),
                MatchValidator(
                    targetTextProvider = { password.text },
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
                email = fields.email.text,
                name = fields.name.text,
                surname = fields.surname.text,
                username = fields.username.text,
                password = fields.password.text
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
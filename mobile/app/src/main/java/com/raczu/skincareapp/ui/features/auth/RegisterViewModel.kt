package com.raczu.skincareapp.ui.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.validation.CompositeValidator
import com.raczu.skincareapp.data.domain.validation.rules.EmailValidator
import com.raczu.skincareapp.data.domain.validation.rules.MatchValidator
import com.raczu.skincareapp.data.domain.validation.rules.PasswordValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.UserRepository
import com.raczu.skincareapp.ui.common.TextFieldState
import kotlinx.coroutines.launch


data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var uiState by mutableStateOf(RegisterUiState())
        private set

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
        if (uiState.error != null) {
            uiState = uiState.copy(error = null)
        }
    }
    val fields = RegisterFields(onChanged = onFieldChanged)

    fun register() {
        if (!fields.validateAll()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val user = UserRegistration(
                email = fields.email.text,
                name = fields.name.text,
                surname = fields.surname.text,
                username = fields.username.text,
                password = fields.password.text
            )

            val result = userRepository.register(user)
            result.onSuccess { _ ->
                uiState = uiState.copy(
                    isLoading = false,
                    isRegistrationSuccessful = true
                )
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }

                uiState = uiState.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }

    }
}
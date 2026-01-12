package com.raczu.skincareapp.ui.features.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.validation.CompositeValidator
import com.raczu.skincareapp.data.domain.validation.rules.EmailValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.AuthRepository
import com.raczu.skincareapp.ui.common.TextFieldState
import kotlinx.coroutines.launch


data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    class LoginFields(onChanged: () -> Unit) {
        val email = TextFieldState(
            validator = CompositeValidator(
                RequiredValidator("Email is required"),
                EmailValidator
            ),
            onValueChangeCallback = onChanged
        )
        val password = TextFieldState(
            validator = RequiredValidator("Password is required"),
            onValueChangeCallback = onChanged
        )

        fun validateAll() = listOf(email, password).map { it.validate() }.all { it }
    }

    private val onFieldChanged = {
        if (uiState.error != null) {
            uiState = uiState.copy(error = null)
        }
    }
    val fields = LoginFields(onChanged = onFieldChanged)

    fun login() {
        if (!fields.validateAll()) return

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = authRepository.login(
                email = fields.email.text,
                password = fields.password.text
            )

            result.onSuccess {
                uiState = uiState.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> {
                        exception.problem.errors?.firstOrNull()?.message ?: exception.problem.detail
                    }
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
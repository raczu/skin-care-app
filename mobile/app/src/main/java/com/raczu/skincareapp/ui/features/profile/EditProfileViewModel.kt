package com.raczu.skincareapp.ui.features.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.domain.validation.CompositeValidator
import com.raczu.skincareapp.data.domain.validation.rules.EmailValidator
import com.raczu.skincareapp.data.domain.validation.rules.RequiredValidator
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.UserRepository
import com.raczu.skincareapp.ui.common.TextFieldState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdateSuccessful: Boolean = false
)

class EditProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    var uiState by mutableStateOf(EditProfileUiState())
        private set

    class EditProfileFields(onChanged: () -> Unit) {
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

        fun validateAll() = listOf(email, name, surname).map { it.validate() }.all { it }
    }

    private val onFieldChanged = {
        if (uiState.error != null) {
            uiState = uiState.copy(error = null)
        }
    }
    val fields = EditProfileFields(onChanged = onFieldChanged)

    init {
        viewModelScope.launch {
            userRepository.user
                .filterNotNull()
                .first()
                .let { fillFields(it) }
        }
        fetchUserProfile()
    }

    private fun fillFields(user: User) {
        fields.name.text = user.name
        fields.surname.text = user.surname
        fields.email.text = user.email
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val result = userRepository.getUserProfile()
            result.onSuccess {
                uiState = uiState.copy(isLoading = false)
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }
                uiState = uiState.copy(isLoading = false, error = errorMessage)
            }
        }
    }

    fun saveChanges() {
        if (!fields.validateAll()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            val update = UserUpdate(
                name = fields.name.text,
                surname = fields.surname.text,
                email = fields.email.text
            )

            val result = userRepository.updateUser(update)
            result.onSuccess {
                uiState = uiState.copy(isLoading = false, isUpdateSuccessful = true)
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }
                uiState = uiState.copy(isLoading = false, error = errorMessage)
            }
        }
    }
}
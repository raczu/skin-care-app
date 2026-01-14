package com.raczu.skincareapp.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.domain.validation.CompositeValidator
import com.raczu.skincareapp.data.domain.validation.rules.EmailValidator
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

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdateSuccessful: Boolean = false
)

class EditProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    class EditProfileFields(onChanged: () -> Unit) {
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

        fun validateAll() = listOf(email, name, surname).map { it.validate() }.all { it }
    }

    private val onFieldChanged = {
        if (_uiState.value.error != null) {
            _uiState.update { it.copy(error = null) }
        }
    }
    val fields = EditProfileFields(onChanged = onFieldChanged)

    init {
        initializeUserData()
    }

    private fun initializeUserData() {
        viewModelScope.launch {
            val cachedUser = userRepository.user.value
            if (cachedUser != null) {
                fillFields(cachedUser)
            } else {
                fetchUserProfile()
            }
        }
    }

    private fun fillFields(user: User) {
        fields.name.value = user.name
        fields.surname.value = user.surname
        fields.email.value = user.email
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getUser()
            result.onSuccess { user ->
                fillFields(user)
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun saveChanges() {
        if (!fields.validateAll()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val update = UserUpdate(
                name = fields.name.value,
                surname = fields.surname.value,
                email = fields.email.value
            )

            val result = userRepository.updateUser(update)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isUpdateSuccessful = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }
}
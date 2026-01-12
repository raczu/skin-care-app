package com.raczu.skincareapp.ui.features.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class UserProfileViewModel(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getUserProfile()
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is RemoteException.ApiError -> exception.problem.detail
                    is RemoteException.NetworkError -> exception.message
                    else -> "An unexpected error occurred"
                }
                _uiState.update {
                    it.copy(isLoading = false, error = errorMessage)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            tokenManager.clearTokens()
        }
    }
}
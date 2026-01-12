package com.raczu.skincareapp.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = userRepository.user
        .map { user -> ProfileUiState(user = user, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileUiState(isLoading = true)
        )

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getUserProfile()
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
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
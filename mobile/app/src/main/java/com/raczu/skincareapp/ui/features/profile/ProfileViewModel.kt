package com.raczu.skincareapp.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.repository.UserRepository
import com.raczu.skincareapp.ui.common.toUiErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

private data class ProfileLoadState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _loadState = MutableStateFlow(ProfileLoadState())
    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.user,
        _loadState
    ) { user, loadState ->
        ProfileUiState(
            isLoading = loadState.isLoading,
            error = loadState.error,
            user = user
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000L),
        initialValue = ProfileUiState(isLoading = true)
    )

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _loadState.update { it.copy(isLoading = true, error = null) }
            val result = userRepository.getUser()
            result.onSuccess {
                _loadState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _loadState.update {
                    it.copy(isLoading = false, error = exception.toUiErrorMessage())
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loadState.update { it.copy(isLoading = true) }
            tokenManager.clearTokens()
            _loadState.update { it.copy(isLoading = false) }
        }
    }
}
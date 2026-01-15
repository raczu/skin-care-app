package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.api.AuthApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall

class RemoteAuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        val result = safeApiCall { authApiService.login(email, password) }

        return result.map { response ->
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }
    }

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}
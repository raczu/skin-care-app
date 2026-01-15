package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.dto.auth.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Route
import okhttp3.Response
import okhttp3.Request
import javax.inject.Provider

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val authApiServiceProvider: Provider<AuthApiService>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val currentToken = runBlocking { tokenManager.accessToken.first() }
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            if (currentToken != requestToken && currentToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }
            val refreshToken = runBlocking { tokenManager.refreshToken.first() } ?: return null
            val res = runBlocking {
                authApiServiceProvider.get().refreshToken(RefreshTokenRequest(refreshToken))
            }

            return if (res.isSuccessful && res.body() != null) {
                val newTokens = res.body()!!
                runBlocking {
                    tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                }

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                runBlocking { tokenManager.clearTokens() }
                null
            }
        }
    }
}
package com.raczu.skincareapp.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "secure_user_prefs")

class TokenManager(
    private val context: Context,
    private val securityProvider: SecurityProvider
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { prefs ->
        securityProvider.decrypt(prefs[ACCESS_TOKEN_KEY])
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { prefs ->
        securityProvider.decrypt(prefs[REFRESH_TOKEN_KEY])
    }

    val isUserLoggedIn: Flow<Boolean> = accessToken.map { token ->
        !token.isNullOrBlank()
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = securityProvider.encrypt(accessToken)
            prefs[REFRESH_TOKEN_KEY] = securityProvider.encrypt(refreshToken)
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }
}
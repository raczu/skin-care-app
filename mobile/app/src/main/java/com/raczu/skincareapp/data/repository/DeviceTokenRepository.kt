package com.raczu.skincareapp.data.repository

interface DeviceTokenRepository {
    suspend fun saveCurrentToken(token: String): Result<Unit>
}

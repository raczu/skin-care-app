package com.raczu.skincareapp.data.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}



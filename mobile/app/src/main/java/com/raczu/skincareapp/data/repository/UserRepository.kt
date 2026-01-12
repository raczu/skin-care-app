package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate

interface UserRepository {
    suspend fun register(user: UserRegistration): Result<User>

    suspend fun getUserProfile(): Result<User>

    suspend fun updateUser(update: UserUpdate): Result<User>
}
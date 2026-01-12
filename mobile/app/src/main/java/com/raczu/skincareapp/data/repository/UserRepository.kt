package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<User?>

    suspend fun register(user: UserRegistration): Result<User>
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUser(update: UserUpdate): Result<User>
}
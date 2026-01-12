package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.remote.api.UserApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import com.raczu.skincareapp.data.remote.dto.user.RegisterRequest
import com.raczu.skincareapp.data.remote.dto.user.UserUpdateRequest

class RemoteUserRepository(
    private val userApiService: UserApiService
) : UserRepository {
    override suspend fun register(user: UserRegistration): Result<User> {
        val request = RegisterRequest(
            email = user.email,
            name = user.name,
            surname = user.surname,
            username = user.username,
            password = user.password
        )
        val result = safeApiCall { userApiService.register(request) }

        return result.map { response ->
            User(
                id = response.id,
                email = response.email,
                name = response.name,
                surname = response.surname,
                username = response.username
            )
        }
    }

    override suspend fun getUserProfile(): Result<User> {
        val result = safeApiCall { userApiService.getUserProfile() }

        return result.map { response ->
            User(
                id = response.id,
                email = response.email,
                name = response.name,
                surname = response.surname,
                username = response.username
            )
        }
    }

    override suspend fun updateUser(update: UserUpdate): Result<User> {
        val request = UserUpdateRequest(
            email = update.email,
            name = update.name,
            surname = update.surname
        )
        val result = safeApiCall { userApiService.updateUser(request) }

        return result.map { response ->
            User(
                id = response.id,
                email = response.email,
                name = response.name,
                surname = response.surname,
                username = response.username
            )
        }
    }
}
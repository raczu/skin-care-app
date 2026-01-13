package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.remote.api.UserApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import com.raczu.skincareapp.data.remote.dto.user.RegisterRequest
import com.raczu.skincareapp.data.remote.dto.user.UserUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RemoteUserRepository(
    private val userApiService: UserApiService
) : UserRepository {
    private val _user = MutableStateFlow<User?>(null)
    override val user: StateFlow<User?> = _user.asStateFlow()

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

    override suspend fun getUser(): Result<User> {
        val result = safeApiCall { userApiService.getUser() }

        return result.map { response ->
            val domainUser = User(
                id = response.id,
                email = response.email,
                name = response.name,
                surname = response.surname,
                username = response.username
            )
            _user.value = domainUser
            domainUser
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
            val domainUser = User(
                id = response.id,
                email = response.email,
                name = response.name,
                surname = response.surname,
                username = response.username
            )
            _user.value = domainUser
            domainUser
        }
    }

    override fun clearData() {
        _user.value = null
    }
}
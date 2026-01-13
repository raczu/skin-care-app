package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.mappers.toDomain
import com.raczu.skincareapp.data.mappers.toRequest
import com.raczu.skincareapp.data.remote.api.UserApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteUserRepository(
    private val userApiService: UserApiService
) : UserRepository {
    private val _user = MutableStateFlow<User?>(null)
    override val user: StateFlow<User?> = _user.asStateFlow()

    override suspend fun register(user: UserRegistration): Result<User> {
        val request = user.toRequest()
        val result = safeApiCall { userApiService.register(request) }

        return result.map { response ->
            response.toDomain()
        }
    }

    override suspend fun getUser(): Result<User> {
        val result = safeApiCall { userApiService.getUser() }

        return result.map { response ->
            val domainUser = response.toDomain()
            _user.update { domainUser }
            domainUser
        }
    }

    override suspend fun updateUser(update: UserUpdate): Result<User> {
        val request = update.toRequest()
        val result = safeApiCall { userApiService.updateUser(request) }

        return result.map { response ->
            val domainUser = response.toDomain()
            _user.update { domainUser }
            domainUser
        }
    }

    override fun clearData() {
        _user.update { null }
    }
}
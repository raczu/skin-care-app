package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.domain.models.user.UserRegistration
import com.raczu.skincareapp.data.domain.models.user.UserUpdate
import com.raczu.skincareapp.data.remote.dto.user.RegisterRequest
import com.raczu.skincareapp.data.remote.dto.user.UserResponse
import com.raczu.skincareapp.data.remote.dto.user.UserUpdateRequest

fun UserResponse.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        surname = this.surname,
        username = this.username,
    )
}

fun UserRegistration.toRequest(): RegisterRequest {
    return RegisterRequest(
        email = this.email,
        name = this.name,
        surname = this.surname,
        username = this.username,
        password = this.password
    )
}

fun UserUpdate.toRequest(): UserUpdateRequest {
    return UserUpdateRequest(
        email = this.email,
        name = this.name,
        surname = this.surname
    )
}
package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.user.User
import com.raczu.skincareapp.data.remote.dto.user.UserResponse

fun UserResponse.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        surname = this.surname,
        username = this.username,
    )
}
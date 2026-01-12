package com.raczu.skincareapp.data.remote.dto.user

data class UserUpdateRequest(
    val email: String? = null,
    val name: String? = null,
    val surname: String? = null
)

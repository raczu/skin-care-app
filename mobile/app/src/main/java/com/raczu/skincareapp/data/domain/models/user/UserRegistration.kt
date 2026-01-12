package com.raczu.skincareapp.data.domain.models.user

data class UserRegistration(
    val email: String,
    val name: String,
    val surname: String,
    val username: String,
    val password: String
)

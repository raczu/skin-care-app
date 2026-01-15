package com.raczu.skincareapp.data.remote.dto.user

data class RegisterRequest(
    val email: String,
    val name: String,
    val surname: String,
    val username: String,
    val password: String
)
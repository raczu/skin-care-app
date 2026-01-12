package com.raczu.skincareapp.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val surname: String,
    val username: String,

    @SerializedName("created_at")
    val createdAt: String
)

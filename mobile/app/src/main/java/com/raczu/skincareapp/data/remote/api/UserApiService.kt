package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.user.RegisterRequest
import com.raczu.skincareapp.data.remote.dto.user.UserResponse
import com.raczu.skincareapp.data.remote.dto.user.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApiService {
    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>


    @GET("users/me")
    suspend fun getUserProfile(): Response<UserResponse>

    @PATCH("users/me")
    suspend fun updateUser(@Body request: UserUpdateRequest): Response<UserResponse>
}
package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.auth.AuthResponse
import com.raczu.skincareapp.data.remote.dto.auth.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApiService {
    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
}
package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.notification.DeviceTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceTokenApiService {
    @POST("devices")
    suspend fun saveDeviceToken(@Body request: DeviceTokenRequest): Response<Unit>
}
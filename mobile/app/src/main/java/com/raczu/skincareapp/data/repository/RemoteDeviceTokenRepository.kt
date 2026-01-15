package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.remote.DeviceMetaUtils
import com.raczu.skincareapp.data.remote.api.DeviceTokenApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import com.raczu.skincareapp.data.remote.dto.notification.DeviceTokenRequest

class RemoteDeviceTokenRepository(
    private val deviceTokenApiService: DeviceTokenApiService
) : DeviceTokenRepository {
    override suspend fun saveCurrentToken(token: String): Result<Unit> {
        val meta = DeviceMetaUtils.getDeviceMeta()
        val request = DeviceTokenRequest(meta = meta, fcmToken = token)

        val result = safeApiCall { deviceTokenApiService.saveDeviceToken(request) }
        return result.map {
            Unit
        }
    }
}
package com.raczu.skincareapp.data.remote.dto.notification

import com.google.gson.annotations.SerializedName

data class DeviceTokenRequest(
    val meta: String,

    @SerializedName("fcm_token")
    val fcmToken: String
)

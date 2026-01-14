package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.GenericMultipleItemsResponse
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleCreateRequest
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleResponse
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationRuleApiService {
    @POST("notification-rules")
    suspend fun addNotification(
        @Body request: NotificationRuleCreateRequest
    ): Response<NotificationRuleResponse>

    @GET("notification-rules")
    suspend fun getNotificationRules(): Response<GenericMultipleItemsResponse<NotificationRuleResponse>>

    @DELETE("notification-rules/{id}")
    suspend fun deleteNotificationRule(
        @Path("id") ruleId: String
    ): Response<Unit>

    @PATCH("notification-rules/{id}")
    suspend fun updateNotificationRule(
        @Path("id") ruleId: String,
        @Body request: NotificationRuleUpdateRequest
    ): Response<NotificationRuleResponse>

    @GET("notification-rules/{id}")
    suspend fun getNotificationRuleDetails(
        @Path("id") ruleId: String
    ): Response<NotificationRuleResponse>
}
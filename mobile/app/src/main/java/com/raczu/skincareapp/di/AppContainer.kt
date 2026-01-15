package com.raczu.skincareapp.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.raczu.skincareapp.BuildConfig
import com.raczu.skincareapp.data.local.preferences.SecurityProvider
import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.ExplicitNullTypeAdapterFactory
import com.raczu.skincareapp.data.remote.InstantAdapter
import com.raczu.skincareapp.data.remote.OffsetTimeAdapter
import com.raczu.skincareapp.data.remote.RuntimeTypeAdapterFactory
import com.raczu.skincareapp.data.remote.api.AuthApiService
import com.raczu.skincareapp.data.remote.api.AuthInterceptor
import com.raczu.skincareapp.data.remote.api.DeviceTokenApiService
import com.raczu.skincareapp.data.remote.api.NotificationRuleApiService
import com.raczu.skincareapp.data.remote.api.ProductApiService
import com.raczu.skincareapp.data.remote.api.RoutineApiService
import com.raczu.skincareapp.data.remote.api.TokenAuthenticator
import com.raczu.skincareapp.data.remote.api.UserApiService
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleCreateRequest
import com.raczu.skincareapp.data.remote.dto.notification.NotificationRuleResponse
import com.raczu.skincareapp.data.repository.AuthRepository
import com.raczu.skincareapp.data.repository.DeviceTokenRepository
import com.raczu.skincareapp.data.repository.NotificationRuleRepository
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.data.repository.RemoteAuthRepository
import com.raczu.skincareapp.data.repository.RemoteDeviceTokenRepository
import com.raczu.skincareapp.data.repository.RemoteNotificationRuleRepository
import com.raczu.skincareapp.data.repository.RemoteProductRepository
import com.raczu.skincareapp.data.repository.RemoteRoutineRepository
import com.raczu.skincareapp.data.repository.RemoteUserRepository
import com.raczu.skincareapp.data.repository.RoutineRepository
import com.raczu.skincareapp.data.repository.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.OffsetTime

class AppContainer(private val context: Context) {
    private val securityProvider by lazy {
        SecurityProvider(context)
    }

    val tokenManager: TokenManager by lazy {
        TokenManager(context, securityProvider)
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.value.create(AuthApiService::class.java)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactHeader("Authorization")
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .authenticator(
                TokenAuthenticator(
                    tokenManager = tokenManager,
                    authApiServiceProvider = { authApiService }
                )
            )
            .build()
    }

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .registerTypeAdapter(OffsetTime::class.java, OffsetTimeAdapter())
        .registerTypeAdapterFactory(ExplicitNullTypeAdapterFactory())
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory
                .of(NotificationRuleResponse::class.java, "frequency")
                .registerSubtype(NotificationRuleResponse.Once::class.java, "ONCE")
                .registerSubtype(NotificationRuleResponse.Daily::class.java, "DAILY")
                .registerSubtype(NotificationRuleResponse.WeekdayOnly::class.java, "WEEKDAY_ONLY")
                .registerSubtype(NotificationRuleResponse.EveryNDays::class.java, "EVERY_N_DAYS")
                .registerSubtype(NotificationRuleResponse.Custom::class.java, "CUSTOM")
        )
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory
                .of(NotificationRuleCreateRequest::class.java, "frequency")
                .registerSubtype(NotificationRuleCreateRequest.Once::class.java, "ONCE")
                .registerSubtype(NotificationRuleCreateRequest.Daily::class.java, "DAILY")
                .registerSubtype(NotificationRuleCreateRequest.WeekdayOnly::class.java, "WEEKDAY_ONLY")
                .registerSubtype(NotificationRuleCreateRequest.EveryNDays::class.java, "EVERY_N_DAYS")
                .registerSubtype(NotificationRuleCreateRequest.Custom::class.java, "CUSTOM")
        )
        .create()

    private val retrofit = lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SKIN_CARE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authRepository: AuthRepository by lazy {
        RemoteAuthRepository(authApiService, tokenManager)
    }

    private val userApiService: UserApiService by lazy {
        retrofit.value.create(UserApiService::class.java)
    }

    val userRepository: UserRepository by lazy {
        RemoteUserRepository(userApiService)
    }

    private val productApiService: ProductApiService by lazy {
        retrofit.value.create(ProductApiService::class.java)
    }

    val productRepository: ProductRepository by lazy {
        RemoteProductRepository(productApiService)
    }

    private val routineApiService: RoutineApiService by lazy {
        retrofit.value.create(RoutineApiService::class.java)
    }

    val routineRepository: RoutineRepository by lazy {
        RemoteRoutineRepository(routineApiService)
    }

    private val notificationRuleApiService: NotificationRuleApiService by lazy {
        retrofit.value.create(NotificationRuleApiService::class.java)
    }

    val routineNotificationRepository: NotificationRuleRepository by lazy {
        RemoteNotificationRuleRepository(notificationRuleApiService)
    }

    private val deviceTokenApiService: DeviceTokenApiService by lazy {
        retrofit.value.create(DeviceTokenApiService::class.java)
    }

    val deviceTokenRepository: DeviceTokenRepository by lazy {
        RemoteDeviceTokenRepository(deviceTokenApiService)
    }
}
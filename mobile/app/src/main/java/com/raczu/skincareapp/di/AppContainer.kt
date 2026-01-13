package com.raczu.skincareapp.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.raczu.skincareapp.BuildConfig
import com.raczu.skincareapp.data.local.entities.AppDatabase
import com.raczu.skincareapp.data.local.preferences.SecurityProvider
import com.raczu.skincareapp.data.local.preferences.TokenManager
import com.raczu.skincareapp.data.remote.InstantAdapter
import com.raczu.skincareapp.data.remote.api.AuthApiService
import com.raczu.skincareapp.data.remote.api.AuthInterceptor
import com.raczu.skincareapp.data.remote.api.ProductApiService
import com.raczu.skincareapp.data.remote.api.TokenAuthenticator
import com.raczu.skincareapp.data.remote.api.UserApiService
import com.raczu.skincareapp.data.repository.AuthRepository
import com.raczu.skincareapp.data.repository.ProductRepository
import com.raczu.skincareapp.data.repository.ProductRepositoryOld
import com.raczu.skincareapp.data.repository.RemoteAuthRepository
import com.raczu.skincareapp.data.repository.RemoteProductRepository
import com.raczu.skincareapp.data.repository.RemoteUserRepository
import com.raczu.skincareapp.data.repository.RoutineNotificationRepository
import com.raczu.skincareapp.data.repository.RoutineRepository
import com.raczu.skincareapp.data.repository.UserRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant

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

    val routineRepository: RoutineRepository by lazy {
        RoutineRepository(AppDatabase.Companion.getDatabase(context).routineDao())
    }

    val routineNotificationRepository: RoutineNotificationRepository by lazy {
        RoutineNotificationRepository(
            AppDatabase.Companion.getDatabase(context).routineNotificationDao()
        )
    }
}
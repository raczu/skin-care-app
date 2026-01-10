package com.raczu.skincareapp.di

import android.content.Context
import com.raczu.skincareapp.data.local.entities.AppDatabase
import com.raczu.skincareapp.data.repositories.ProductRepository
import com.raczu.skincareapp.data.repositories.RoutineNotificationRepository
import com.raczu.skincareapp.data.repositories.RoutineRepository

class AppContainer(private val context: Context) {
    val routineRepository: RoutineRepository by lazy {
        RoutineRepository(AppDatabase.Companion.getDatabase(context).routineDao())
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(AppDatabase.Companion.getDatabase(context).productDao())
    }

    val routineNotificationRepository: RoutineNotificationRepository by lazy {
        RoutineNotificationRepository(
            AppDatabase.Companion.getDatabase(context).routineNotificationDao()
        )
    }
}
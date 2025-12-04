package com.raczu.skincareapp

import android.content.Context
import com.raczu.skincareapp.entities.AppDatabase
import com.raczu.skincareapp.repositories.ProductRepository
import com.raczu.skincareapp.repositories.RoutineNotificationRepository
import com.raczu.skincareapp.repositories.RoutineRepository

class AppContainer(private val context: Context) {
    val routineRepository: RoutineRepository by lazy {
        RoutineRepository(AppDatabase.getDatabase(context).routineDao())
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(AppDatabase.getDatabase(context).productDao())
    }

    val routineNotificationRepository: RoutineNotificationRepository by lazy {
        RoutineNotificationRepository(AppDatabase.getDatabase(context).routineNotificationDao())
    }
}
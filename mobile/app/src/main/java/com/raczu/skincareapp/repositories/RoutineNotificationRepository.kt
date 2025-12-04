package com.raczu.skincareapp.repositories

import com.raczu.skincareapp.daos.RoutineNotificationDao
import com.raczu.skincareapp.entities.RoutineNotification
import com.raczu.skincareapp.enums.RoutineType
import kotlinx.coroutines.flow.Flow

class RoutineNotificationRepository(private val routineNotificationDao: RoutineNotificationDao) {
    suspend fun insert(notification: RoutineNotification) = routineNotificationDao.insert(notification)

    suspend fun update(notification: RoutineNotification) = routineNotificationDao.update(notification)

    suspend fun delete(notification: RoutineNotification) = routineNotificationDao.delete(notification)

    fun getNotification(type: RoutineType): Flow<RoutineNotification?> {
        return routineNotificationDao.getNotification(type)
    }
}
package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.local.daos.RoutineNotificationDao
import com.raczu.skincareapp.data.local.entities.RoutineNotification
import com.raczu.skincareapp.utils.enums.RoutineType
import kotlinx.coroutines.flow.Flow

class RoutineNotificationRepository(private val routineNotificationDao: RoutineNotificationDao) {
    suspend fun insert(notification: RoutineNotification) = routineNotificationDao.insert(notification)

    suspend fun update(notification: RoutineNotification) = routineNotificationDao.update(notification)

    suspend fun delete(notification: RoutineNotification) = routineNotificationDao.delete(notification)

    fun getNotification(type: RoutineType): Flow<RoutineNotification?> {
        return routineNotificationDao.getNotification(type)
    }
}
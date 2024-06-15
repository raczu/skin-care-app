package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.raczu.skincareapp.entities.RoutineNotification
import com.raczu.skincareapp.enums.RoutineType
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineNotificationDao {
    @Insert
    suspend fun insert(notification: RoutineNotification)

    @Update
    suspend fun update(notification: RoutineNotification)

    @Delete
    suspend fun delete(notification: RoutineNotification)

    @Query("SELECT * FROM routine_notification WHERE type = :type")
    fun getNotification(type: RoutineType): Flow<RoutineNotification?>
}
package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.raczu.skincareapp.entities.RoutineNotification
import com.raczu.skincareapp.enums.RoutineType

@Dao
interface RoutineNotificationDao {
    @Insert
    fun insert(notification: RoutineNotification)

    @Update
    fun update(notification: RoutineNotification)

    @Delete
    fun delete(notification: RoutineNotification)

    @Query("SELECT * FROM routine_notification WHERE type = :type")
    fun getNotification(type: RoutineType): RoutineNotification
}
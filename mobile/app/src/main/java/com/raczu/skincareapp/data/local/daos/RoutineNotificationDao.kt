package com.raczu.skincareapp.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raczu.skincareapp.data.local.entities.RoutineNotification
import com.raczu.skincareapp.utils.enums.RoutineType
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: RoutineNotification)

    @Update
    suspend fun update(notification: RoutineNotification)

    @Delete
    suspend fun delete(notification: RoutineNotification)

    @Query("SELECT * FROM routine_notification WHERE type = :type")
    fun getNotification(type: RoutineType): Flow<RoutineNotification?>
}
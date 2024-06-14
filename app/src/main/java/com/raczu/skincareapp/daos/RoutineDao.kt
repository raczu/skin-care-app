package com.raczu.skincareapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.raczu.skincareapp.entities.Routine
import com.raczu.skincareapp.entities.RoutineWithProducts

@Dao
interface RoutineDao {
    @Insert
    fun insert(routine: Routine)

    @Update
    fun update(routine: Routine)

    @Delete
    fun delete(routine: Routine)

    @Transaction
    @Query("SELECT * FROM routine WHERE routine_id = :id")
    fun getRoutineWithProducts(id: Int): RoutineWithProducts

    @Query("SELECT * FROM routine ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    fun getRoutinesWithCursor(limit: Int = 15, offset: Int = 0): List<Routine>

    @Query("SELECT * FROM routine WHERE DATE(created_at) = DATE('now')")
    fun getTodayRoutines(): List<Routine>
}
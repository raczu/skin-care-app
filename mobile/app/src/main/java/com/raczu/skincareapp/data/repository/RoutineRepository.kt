package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.domain.models.routine.RoutineCreate
import com.raczu.skincareapp.data.domain.models.routine.RoutineUpdate
import com.raczu.skincareapp.data.domain.models.routine.RoutinesPage
import java.time.LocalDateTime

interface RoutineRepository {
    suspend fun getRoutines(
        limit: Int = 15,
        offset: Int = 0,
        performedAfter: LocalDateTime? = null,
        performedBefore: LocalDateTime? = null
    ): Result<RoutinesPage>

    suspend fun getRoutineDetails(routineId: String): Result<Routine>
    suspend fun addRoutine(routine: RoutineCreate): Result<Routine>
    suspend fun updateRoutine(routineId: String, routine: RoutineUpdate): Result<Routine>
    suspend fun deleteRoutine(routineId: String): Result<Unit>
}
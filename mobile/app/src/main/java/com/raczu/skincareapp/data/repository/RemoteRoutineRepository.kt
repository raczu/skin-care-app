package com.raczu.skincareapp.data.repository

import com.raczu.skincareapp.data.domain.models.routine.RoutineCreate
import com.raczu.skincareapp.data.domain.models.routine.RoutineUpdate
import com.raczu.skincareapp.data.domain.models.routine.RoutinesPage
import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.mappers.toDomain
import com.raczu.skincareapp.data.mappers.toRequest
import com.raczu.skincareapp.data.remote.api.RoutineApiService
import com.raczu.skincareapp.data.remote.api.safeApiCall
import com.raczu.skincareapp.data.remote.api.toIsoString
import java.time.LocalDateTime

class RemoteRoutineRepository(
    private val routineApiService: RoutineApiService
) : RoutineRepository {
    override suspend fun getRoutines(
        limit: Int,
        offset: Int,
        performedAfter: LocalDateTime?,
        performedBefore: LocalDateTime?
    ): Result<RoutinesPage> {
        val afterString = performedAfter?.toIsoString()
        val beforeString = performedBefore?.toIsoString()

        val result = safeApiCall {
            routineApiService.getRoutines(
                limit = limit,
                offset = offset,
                performedAfter = afterString,
                performedBefore = beforeString
            )
        }

        return result.map { response ->
            RoutinesPage(
                routines = response.items.map { it.toDomain() },
                hasMore = (response.pagination.offset + response.pagination.limit) < response.meta.total
            )
        }
    }

    override suspend fun getRoutineDetails(routineId: String): Result<Routine> {
        val result = safeApiCall { routineApiService.getRoutineDetails(routineId) }
        return result.map { response ->
            response.toDomain()
        }
    }

    override suspend fun addRoutine(routine: RoutineCreate): Result<Routine> {
        val request = routine.toRequest()
        val result = safeApiCall { routineApiService.addRoutine(request) }
        return result.map { response ->
            response.toDomain()
        }
    }

    override suspend fun updateRoutine(routineId: String, routine: RoutineUpdate): Result<Routine> {
        val request = routine.toRequest()
        val result = safeApiCall { routineApiService.updateRoutine(routineId, request) }
        return result.map { response ->
            response.toDomain()
        }
    }

    override suspend fun deleteRoutine(routineId: String): Result<Unit> {
        return safeApiCall { routineApiService.deleteRoutine(routineId) }
    }
}
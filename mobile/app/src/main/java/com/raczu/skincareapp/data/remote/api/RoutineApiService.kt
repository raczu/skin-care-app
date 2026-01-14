package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.dto.PagedResponse
import com.raczu.skincareapp.data.remote.dto.routine.RoutineCreateRequest
import com.raczu.skincareapp.data.remote.dto.routine.RoutineResponse
import com.raczu.skincareapp.data.remote.dto.routine.RoutineUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutineApiService {
    @POST("routines")
    suspend fun addRoutine(@Body request: RoutineCreateRequest): Response<RoutineResponse>

    @GET("routines")
    suspend fun getRoutines(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("performed_after") performedAfter: String? = null,
        @Query("performed_before") performedBefore: String? = null
    ): Response<PagedResponse<RoutineResponse>>

    @DELETE("routines/{id}")
    suspend fun deleteRoutine(@Path("id") routineId: String): Response<Unit>

    @PATCH("routines/{id}")
    suspend fun updateRoutine(
        @Path("id") routineId: String,
        @Body request: RoutineUpdateRequest
    ): Response<RoutineResponse>

    @GET("routines/{id}")
    suspend fun getRoutineDetails(@Path("id") routineId: String): Response<RoutineResponse>
}
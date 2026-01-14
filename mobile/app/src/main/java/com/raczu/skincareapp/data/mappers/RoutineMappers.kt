package com.raczu.skincareapp.data.mappers

import com.raczu.skincareapp.data.domain.models.routine.Routine
import com.raczu.skincareapp.data.domain.models.routine.RoutineCreate
import com.raczu.skincareapp.data.domain.models.routine.RoutineType
import com.raczu.skincareapp.data.domain.models.routine.RoutineUpdate
import com.raczu.skincareapp.data.remote.ExplicitNull
import com.raczu.skincareapp.data.remote.api.toIsoString
import com.raczu.skincareapp.data.remote.dto.routine.RoutineCreateRequest
import com.raczu.skincareapp.data.remote.dto.routine.RoutineResponse
import com.raczu.skincareapp.data.remote.dto.routine.RoutineUpdateRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun RoutineCreate.toRequest(): RoutineCreateRequest {
    return RoutineCreateRequest(
        type = this.type?.name,
        notes = this.notes,
        performedAt = this.performedAt.toIsoString(),
        productIds = this.productIds
    )
}

fun RoutineResponse.toDomain(): Routine {
    return Routine(
        id = this.id,
        type = RoutineType.valueOf(this.type),
        performedAt = LocalDateTime.ofInstant(
            Instant.parse(this.performedAt),
            ZoneId.systemDefault()
        ),
        notes = this.notes,
        products = this.products.map { it.toDomain() }
    )
}

fun RoutineUpdate.toRequest(): RoutineUpdateRequest {
    return RoutineUpdateRequest(
        type = this.type?.name,
        notes = ExplicitNull(this.notes),
        performedAt = this.performedAt?.toIsoString(),
        productIds = this.productIds
    )
}

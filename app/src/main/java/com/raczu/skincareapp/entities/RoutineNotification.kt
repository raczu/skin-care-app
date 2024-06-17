package com.raczu.skincareapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raczu.skincareapp.converters.DateConverter
import com.raczu.skincareapp.converters.RoutineTypeConverter
import com.raczu.skincareapp.enums.RoutineType
import java.time.Instant

@Entity(tableName = "routine_notification")
@TypeConverters(RoutineTypeConverter::class, DateConverter::class)
data class RoutineNotification(
    @PrimaryKey
    val type: RoutineType,
    val time: Instant? = null,
)
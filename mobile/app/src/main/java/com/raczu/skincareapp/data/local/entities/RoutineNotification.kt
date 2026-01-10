package com.raczu.skincareapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raczu.skincareapp.data.local.converters.LocalTimeConverter
import com.raczu.skincareapp.data.local.converters.RoutineTypeConverter
import com.raczu.skincareapp.utils.enums.RoutineType
import java.time.LocalTime

@Entity(tableName = "routine_notification")
@TypeConverters(RoutineTypeConverter::class, LocalTimeConverter::class)
data class RoutineNotification(
    @PrimaryKey
    val type: RoutineType,
    val time: LocalTime? = null,
)
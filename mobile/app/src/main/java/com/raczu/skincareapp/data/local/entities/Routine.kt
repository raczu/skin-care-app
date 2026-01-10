package com.raczu.skincareapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raczu.skincareapp.data.local.converters.DateConverter
import com.raczu.skincareapp.data.local.converters.RoutineTypeConverter
import com.raczu.skincareapp.utils.enums.RoutineType
import java.time.Instant

@Entity(tableName = "routine")
@TypeConverters(RoutineTypeConverter::class, DateConverter::class)
data class Routine(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("routine_id")
    val routineId: Int = 0,
    val type: RoutineType,
    @ColumnInfo("created_at")
    val createdAt: Instant = Instant.now()
)

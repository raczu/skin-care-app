package com.raczu.skincareapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.raczu.skincareapp.converters.DateConverter
import com.raczu.skincareapp.converters.RoutineTypeConverter
import com.raczu.skincareapp.enums.RoutineType
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

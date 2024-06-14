package com.raczu.skincareapp.converters

import androidx.room.TypeConverter
import com.raczu.skincareapp.enums.RoutineType

class RoutineTypeConverter {
    @TypeConverter
    fun toRoutineType(value: String) = enumValueOf<RoutineType>(value)

    @TypeConverter
    fun fromRoutineType(value: RoutineType) = value.name
}
package com.raczu.skincareapp.data.local.converters

import androidx.room.TypeConverter
import com.raczu.skincareapp.utils.enums.RoutineType

class RoutineTypeConverter {
    @TypeConverter
    fun toRoutineType(value: String) = enumValueOf<RoutineType>(value)

    @TypeConverter
    fun fromRoutineType(value: RoutineType) = value.name
}
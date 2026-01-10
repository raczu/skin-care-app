package com.raczu.skincareapp.data.local.converters

import androidx.room.TypeConverter
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?): String? = localTime?.toString()

    @TypeConverter
    fun toLocalTime(localTime: String?): LocalTime? = localTime?.let { LocalTime.parse(it) }
}
package com.raczu.skincareapp.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

class OffsetTimeAdapter : JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {
    private val formatter = DateTimeFormatter.ISO_OFFSET_TIME

    override fun serialize(
        src: OffsetTime,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.format(formatter))
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): OffsetTime {
        return OffsetTime.parse(json.asString, formatter)
    }
}
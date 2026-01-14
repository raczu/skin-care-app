package com.raczu.skincareapp.data.remote

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType

data class ExplicitNull<T>(val value: T?)

class ExplicitNullAdapter<T>(
    private val delegate: TypeAdapter<T>
) : TypeAdapter<ExplicitNull<T>>() {
    override fun write(out: JsonWriter, wrapper: ExplicitNull<T>?) {
        if (wrapper == null) {
            return
        }

        if (wrapper.value == null) {
            out.jsonValue("null")
            return
        }
        delegate.write(out, wrapper.value)
    }

    override fun read(input: JsonReader): ExplicitNull<T>? {
        if (input.peek() == com.google.gson.stream.JsonToken.NULL) {
            input.nextNull()
            return null
        }
        return ExplicitNull(delegate.read(input))
    }
}

class ExplicitNullTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != ExplicitNull::class.java) {
            return null
        }
        val innerType = (type.type as ParameterizedType).actualTypeArguments[0]
        val delegate = gson.getAdapter(TypeToken.get(innerType)) as TypeAdapter<Any>

        @Suppress("UNCHECKED_CAST")
        return ExplicitNullAdapter(delegate) as TypeAdapter<T>
    }
}

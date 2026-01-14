package com.raczu.skincareapp.data.remote.api

import com.google.gson.Gson
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.remote.dto.ProblemDetails
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId

fun <T> Response<T>.toRemoteException() : RemoteException {
    val errorJson = errorBody()?.string()
    return try {
        val problem = Gson().fromJson(errorJson, ProblemDetails::class.java)
        RemoteException.ApiError(
            problem = problem,
            code = code()
        )
    } catch (e: Exception) {
        RemoteException.UnknownError(e)
    }
}

fun LocalDateTime.toIsoString(): String {
    return this.atZone(ZoneId.systemDefault()).toInstant().toString()
}
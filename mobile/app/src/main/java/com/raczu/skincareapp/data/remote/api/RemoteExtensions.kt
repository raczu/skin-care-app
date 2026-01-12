package com.raczu.skincareapp.data.remote.api

import com.google.gson.Gson
import com.raczu.skincareapp.data.remote.RemoteException
import com.raczu.skincareapp.data.remote.dto.ProblemDetails
import retrofit2.Response

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
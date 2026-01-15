package com.raczu.skincareapp.data.remote.api

import com.raczu.skincareapp.data.remote.RemoteException
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            val code = response.code()

            when {
                body != null -> {
                    Result.success(body)
                }
                code == 204 -> {
                    @Suppress("UNCHECKED_CAST")
                    Result.success(Unit as T)
                }
                else -> {
                    Result.failure(
                        RemoteException.UnknownError(
                            IllegalStateException("Response body is null")
                        )
                    )
                }
            }
        } else {
            Result.failure(response.toRemoteException())
        }
    } catch (e: IOException) {
        Result.failure(RemoteException.NetworkError(e))
    } catch (e: Exception) {
        Result.failure(RemoteException.UnknownError(e))
    }
}
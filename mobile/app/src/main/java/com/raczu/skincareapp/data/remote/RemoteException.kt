package com.raczu.skincareapp.data.remote

import com.raczu.skincareapp.data.remote.dto.ProblemDetails

sealed class RemoteException : Exception() {
    data class ApiError(
        val problem: ProblemDetails,
        val code: Int
    ) : RemoteException() {
        override val message: String = problem.detail
    }

    data class NetworkError(val exception: Throwable) : RemoteException() {
        override val message: String = "Network error occurred"
    }

    data class UnknownError(val exception: Throwable) : RemoteException() {
        override val message: String = "An unexpected error occurred"
    }
}
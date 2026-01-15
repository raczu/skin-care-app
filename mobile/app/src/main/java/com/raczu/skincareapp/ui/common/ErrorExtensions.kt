package com.raczu.skincareapp.ui.common

import com.raczu.skincareapp.data.remote.RemoteException

fun Throwable.toUiErrorMessage(): String {
    return when (this) {
        is RemoteException.ApiError -> this.problem.errors?.firstOrNull()?.message ?: this.problem.detail
        is RemoteException.NetworkError -> this.message
        else -> "An unexpected error occurred"
    }
}

package com.raczu.skincareapp.ui.navigation

sealed class AuthScreen(val route: String) {
    data object Login: AuthScreen("login")
    data object Register: AuthScreen("register")
}

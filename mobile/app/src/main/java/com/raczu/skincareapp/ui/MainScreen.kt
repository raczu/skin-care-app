package com.raczu.skincareapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.raczu.skincareapp.di.AppViewModelProvider
import com.raczu.skincareapp.ui.components.BottomBar
import com.raczu.skincareapp.ui.components.FullScreenLoading
import com.raczu.skincareapp.ui.features.main.AuthState
import com.raczu.skincareapp.ui.features.main.MainViewModel
import com.raczu.skincareapp.ui.navigation.AppNavGraph
import com.raczu.skincareapp.ui.navigation.AuthScreen
import com.raczu.skincareapp.ui.navigation.BottomBarScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val navController = rememberNavController()
    val authState by viewModel.authState.collectAsState()

    if (authState is AuthState.Initial) {
        FullScreenLoading()
        return
    }

    val startRoute = if (authState is AuthState.Authenticated) {
        BottomBarScreen.Routine.route
    } else {
        AuthScreen.Login.route
    }

    Scaffold(
        bottomBar = {
            if (authState is AuthState.Authenticated) {
                BottomBar(navController)
            }
        },
    ) {
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(it),
            startDestination = startRoute
        )
    }
}

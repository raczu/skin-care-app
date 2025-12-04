package com.raczu.skincareapp.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.raczu.skincareapp.components.BottomBar
import com.raczu.skincareapp.navigation.AppNavGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController) },
    ) {
        AppNavGraph(navController, Modifier.padding(it))
    }
}

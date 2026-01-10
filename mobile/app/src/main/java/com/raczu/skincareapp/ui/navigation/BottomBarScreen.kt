package com.raczu.skincareapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Routine: BottomBarScreen(
        route = "routine",
        title = "Routine",
        icon = Icons.Default.DateRange
    )
    data object Products: BottomBarScreen(
        route = "products",
        title = "Products",
        icon = Icons.Default.Face
    )
    data object Notifications: BottomBarScreen(
        route = "notifications",
        title = "Notifications",
        icon = Icons.Default.Notifications
    )
}
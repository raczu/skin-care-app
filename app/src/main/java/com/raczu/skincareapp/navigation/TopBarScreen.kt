package com.raczu.skincareapp.navigation


sealed class TopBarScreen(
    val route: String,
    val args: String = "",
    val title: String
) {
    data object RoutineAdd: TopBarScreen(
        route = "routineAdd",
        title = "Add a new routine",
    )
    data object RoutineDetails: TopBarScreen(
        route = "routineDetails/{routineId}",
        args = "routineId",
        title = "Routine details"
    )
    data object ProductAdd: TopBarScreen(
        route = "productAdd",
        title = "Add a new product"
    )
    data object ProductEdit: TopBarScreen(
        route = "productEdit/{productId}",
        args = "productId",
        title = "Edit product details"
    )
}
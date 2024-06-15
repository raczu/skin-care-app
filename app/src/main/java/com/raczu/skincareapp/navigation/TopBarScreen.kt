package com.raczu.skincareapp.navigation


sealed class TopBarScreen(
    val route: String,
    val args: String = "",
    val title: String
) {
    val routeWithArgs: String
        get() = if (args.isNotEmpty()) "$route/{$args}" else route
    data object RoutineAdd: TopBarScreen(
        route = "routineAdd",
        title = "Note down a new routine",
    )
    data object RoutineDetails: TopBarScreen(
        route = "routineDetails",
        args = "routineId",
        title = "Routine details"
    )
    data object ProductAdd: TopBarScreen(
        route = "productAdd",
        title = "Add a new product"
    )
    data object ProductEdit: TopBarScreen(
        route = "productEdit",
        args = "productId",
        title = "Edit product details"
    )
}
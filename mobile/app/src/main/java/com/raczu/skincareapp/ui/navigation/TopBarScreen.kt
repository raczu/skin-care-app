package com.raczu.skincareapp.ui.navigation


sealed class TopBarScreen(
    val route: String,
    val args: String = "",
    val title: String
) {
    val routeWithArgs: String
        get() = if (args.isNotEmpty()) "$route/{$args}" else route
    data object RoutineAdd: TopBarScreen(
        route = "routineAdd",
        title = "Note a new routine for today"
    )
    data object RoutineDetails: TopBarScreen(
        route = "routineDetails",
        args = "routineId",
        title = "Routine details"
    )
    data object AddProduct: TopBarScreen(
        route = "productAdd",
        title = "Add a new product"
    )
    data object EditProduct: TopBarScreen(
        route = "productEdit",
        args = "productId",
        title = "Edit product details"
    )
    data object EditProfile: TopBarScreen(
        route = "profileEdit",
        title = "Edit Profile"
    )
}
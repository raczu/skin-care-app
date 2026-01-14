package com.raczu.skincareapp.ui.navigation


sealed class TopBarScreen(
    val route: String,
    val args: String = "",
    val title: String
) {
    val routeWithArgs: String
        get() = if (args.isNotEmpty()) "$route/{$args}" else route
    data object RoutineDetails: TopBarScreen(
        route = "routine_details",
        args = "routineId",
        title = "Routine details"
    )
    data object AddRoutine: TopBarScreen(
        route = "routine_add",
        title = "Add a new routine"
    )
    data object EditRoutine: TopBarScreen(
        route = "routine_edit",
        args = "routineId",
        title = "Edit routine details"
    )
    data object AddProduct: TopBarScreen(
        route = "product_add",
        title = "Add a new product"
    )
    data object EditProduct: TopBarScreen(
        route = "product_edit",
        args = "productId",
        title = "Edit product details"
    )
    data object EditProfile: TopBarScreen(
        route = "profile_edit",
        title = "Edit Profile"
    )
}
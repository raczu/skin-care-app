package com.raczu.skincareapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.raczu.skincareapp.ui.features.notifications.NotificationsScreen
import com.raczu.skincareapp.ui.features.products.ProductAddScreen
import com.raczu.skincareapp.ui.features.products.ProductEditScreen
import com.raczu.skincareapp.ui.features.products.ProductsScreen
import com.raczu.skincareapp.ui.features.routines.RoutineAddScreen
import com.raczu.skincareapp.ui.features.routines.RoutineDetailsScreen
import com.raczu.skincareapp.ui.features.routines.RoutineScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Routine.route,
        modifier = modifier
    ) {
        composable(route = BottomBarScreen.Routine.route) {
            RoutineScreen(
                title = BottomBarScreen.Routine.title,
                navigateToRoutineAdd = {
                    navController.navigate(TopBarScreen.RoutineAdd.route)
                },
                navigateToRoutineDetails = {
                    navController.navigate("${TopBarScreen.RoutineDetails.route}/$it")
                }
            )
        }
        composable(route = TopBarScreen.RoutineAdd.route) {
            RoutineAddScreen(
                title = TopBarScreen.RoutineAdd.title,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = TopBarScreen.RoutineDetails.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.RoutineDetails.args) {
                type = NavType.IntType
            })
        ) {
            RoutineDetailsScreen(
                title = TopBarScreen.RoutineDetails.title,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = BottomBarScreen.Products.route) {
            ProductsScreen(
                title = BottomBarScreen.Products.title,
                navigateToProductAdd = {
                    navController.navigate(TopBarScreen.ProductAdd.route)
                },
                navigateToProductEdit = {
                    navController.navigate("${TopBarScreen.ProductEdit.route}/$it")
                }
            )
        }
        composable(route = TopBarScreen.ProductAdd.route) {
            ProductAddScreen(
                title = TopBarScreen.ProductAdd.title,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = TopBarScreen.ProductEdit.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.ProductEdit.args) {
                type = NavType.IntType
            })
        ) {
            ProductEditScreen(
                title = TopBarScreen.ProductEdit.title,
                onBack = { navController.popBackStack() }
            )
        }
        composable(route = BottomBarScreen.Notifications.route) {
            NotificationsScreen(
                title = BottomBarScreen.Notifications.title
            )
        }
    }
}
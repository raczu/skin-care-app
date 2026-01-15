package com.raczu.skincareapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.raczu.skincareapp.ui.features.auth.LoginScreen
import com.raczu.skincareapp.ui.features.auth.RegisterScreen
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationFormContent
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationFormScreen
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationsScreen
import com.raczu.skincareapp.ui.features.products.ProductFormScreen
import com.raczu.skincareapp.ui.features.products.ProductsScreen
import com.raczu.skincareapp.ui.features.profile.EditProfileScreen
import com.raczu.skincareapp.ui.features.profile.ProfileScreen
import com.raczu.skincareapp.ui.features.routine.RoutineDetailsScreen
import com.raczu.skincareapp.ui.features.routine.RoutineFormScreen
import com.raczu.skincareapp.ui.features.routine.RoutineScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AuthScreen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = AuthScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(BottomBarScreen.Routine.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AuthScreen.Register.route)
                }
            )
        }
        composable(route = AuthScreen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(AuthScreen.Login.route)
                }
            )
        }
        composable(route = BottomBarScreen.Routine.route) {
            RoutineScreen(
                title = BottomBarScreen.Routine.title,
                navController = navController
            )
        }
        composable(
            route = TopBarScreen.RoutineDetails.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.RoutineDetails.args) {
                type = NavType.StringType
            })
        ) {
            RoutineDetailsScreen(
                title = TopBarScreen.RoutineDetails.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(route = TopBarScreen.AddRoutine.route) {
            RoutineFormScreen(
                title = TopBarScreen.AddRoutine.title,
                navController = navController
            )
        }
        composable(
            route = TopBarScreen.EditRoutine.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.EditRoutine.args) {
                type = NavType.StringType
            })
        ) {
            RoutineFormScreen(
                title = TopBarScreen.EditRoutine.title,
                navController = navController
            )
        }
        composable(route = BottomBarScreen.Products.route) {
            ProductsScreen(
                title = BottomBarScreen.Products.title,
                onNavigateToProductAdd = {
                    navController.navigate(TopBarScreen.AddProduct.route)
                },
                onNavigateToProductEdit = {
                    navController.navigate("${TopBarScreen.EditProduct.route}/$it")
                }
            )
        }
        composable(route = TopBarScreen.AddProduct.route) {
            ProductFormScreen(
                title = TopBarScreen.AddProduct.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = TopBarScreen.EditProduct.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.EditProduct.args) {
                type = NavType.StringType
            })
        ) {
            ProductFormScreen(
                title = TopBarScreen.EditProduct.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(route = BottomBarScreen.Notifications.route) {
            RoutineNotificationsScreen(
                title = BottomBarScreen.Notifications.title,
                onNavigateToNotificationAdd = {
                    navController.navigate(TopBarScreen.AddRoutineNotificationRule.route)
                },
                onNavigateToNotificationEdit = {
                    navController.navigate("${TopBarScreen.EditRoutineNotificationRule.route}/$it")
                }
            )
        }
        composable(route = TopBarScreen.AddRoutineNotificationRule.route) {
            RoutineNotificationFormScreen(
                title = TopBarScreen.AddRoutineNotificationRule.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = TopBarScreen.EditRoutineNotificationRule.routeWithArgs,
            arguments = listOf(navArgument(TopBarScreen.EditRoutineNotificationRule.args) {
                type = NavType.StringType
            })
        ) {
            RoutineNotificationFormScreen(
                title = TopBarScreen.EditRoutineNotificationRule.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(route = BottomBarScreen.Profile.route) {
            ProfileScreen(
                title = BottomBarScreen.Profile.title,
                onNavigateToUserUpdate = {
                    navController.navigate(TopBarScreen.EditProfile.route)
                }
            )
        }
        composable(route = TopBarScreen.EditProfile.route) {
            EditProfileScreen(
                title = TopBarScreen.EditProfile.title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
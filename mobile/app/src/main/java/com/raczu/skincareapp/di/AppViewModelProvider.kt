package com.raczu.skincareapp.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.raczu.skincareapp.SkinCareApplication
import com.raczu.skincareapp.ui.features.auth.LoginViewModel
import com.raczu.skincareapp.ui.features.auth.RegisterViewModel
import com.raczu.skincareapp.ui.features.main.MainViewModel
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationFormViewModel
import com.raczu.skincareapp.ui.features.products.ProductsViewModel
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationsViewModel
import com.raczu.skincareapp.ui.features.products.ProductFormViewModel
import com.raczu.skincareapp.ui.features.profile.EditProfileViewModel
import com.raczu.skincareapp.ui.features.profile.ProfileViewModel
import com.raczu.skincareapp.ui.features.routine.RoutineDetailsViewModel
import com.raczu.skincareapp.ui.features.routine.RoutineFormViewModel
import com.raczu.skincareapp.ui.features.routine.RoutineViewModel
import com.raczu.skincareapp.ui.navigation.TopBarScreen

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            MainViewModel(
                skinCareApplication().container.tokenManager
            )
        }

        initializer {
            LoginViewModel(
                skinCareApplication().container.authRepository
            )
        }

        initializer {
            RegisterViewModel(
                skinCareApplication().container.userRepository
            )
        }

        initializer {
            ProductsViewModel(
                skinCareApplication().container.productRepository
            )
        }

        initializer {
            val savedStateHandle = this.createSavedStateHandle()
            val productId: String? = savedStateHandle[TopBarScreen.EditProduct.args]

            ProductFormViewModel(
                skinCareApplication().container.productRepository,
                productId
            )
        }

        initializer {
            RoutineViewModel(
                skinCareApplication().container.routineRepository
            )
        }

        initializer {
            val savedStateHandle = this.createSavedStateHandle()
            val routineId: String = checkNotNull(
                savedStateHandle[TopBarScreen.RoutineDetails.args]
            )

            RoutineDetailsViewModel(
                skinCareApplication().container.routineRepository,
                routineId
            )
        }

        initializer {
            val savedStateHandle = this.createSavedStateHandle()
            val productId: String? = savedStateHandle[TopBarScreen.EditRoutine.args]

            RoutineFormViewModel(
                skinCareApplication().container.routineRepository,
                skinCareApplication().container.productRepository,
                productId
            )
        }

        initializer {
            RoutineNotificationsViewModel(
                skinCareApplication().container.routineNotificationRepository
            )
        }

        initializer {
            val savedStateHandle = this.createSavedStateHandle()
            val ruleId: String? = savedStateHandle[TopBarScreen.EditRoutineNotificationRule.args]

            RoutineNotificationFormViewModel(
                skinCareApplication().container.routineNotificationRepository,
                ruleId
            )
        }

        initializer {
            ProfileViewModel(
                skinCareApplication().container.userRepository,
                skinCareApplication().container.tokenManager
            )
        }

        initializer {
            EditProfileViewModel(
                skinCareApplication().container.userRepository
            )
        }
    }
}

fun CreationExtras.skinCareApplication(): SkinCareApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SkinCareApplication)
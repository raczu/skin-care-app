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
import com.raczu.skincareapp.ui.features.products.ProductAddViewModel
import com.raczu.skincareapp.ui.features.products.ProductEditViewModel
import com.raczu.skincareapp.ui.features.products.ProductsViewModel
import com.raczu.skincareapp.ui.features.routines.RoutineAddViewModel
import com.raczu.skincareapp.ui.features.routines.RoutineDetailsViewModel
import com.raczu.skincareapp.ui.features.notifications.RoutineNotificationViewModel
import com.raczu.skincareapp.ui.features.profile.EditProfileViewModel
import com.raczu.skincareapp.ui.features.routines.RoutineViewModel
import com.raczu.skincareapp.ui.features.profile.ProfileViewModel

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
            ProductAddViewModel(
                skinCareApplication().container.productRepository
            )
        }

        initializer {
            ProductsViewModel(
                skinCareApplication().container.productRepository
            )
        }

        initializer {
            ProductEditViewModel(
                this.createSavedStateHandle(),
                skinCareApplication().container.productRepository
            )
        }

        initializer {
            RoutineAddViewModel(
                skinCareApplication().container.routineRepository,
                skinCareApplication().container.productRepository
            )
        }

        initializer {
            RoutineViewModel(
                skinCareApplication().container.routineRepository
            )
        }

        initializer {
            RoutineDetailsViewModel(
                this.createSavedStateHandle(),
                skinCareApplication().container.routineRepository
            )
        }

        initializer {
            RoutineNotificationViewModel(
                skinCareApplication().container.routineNotificationRepository
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
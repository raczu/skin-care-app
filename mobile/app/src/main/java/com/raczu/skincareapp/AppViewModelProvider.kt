package com.raczu.skincareapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.raczu.skincareapp.views.ProductAddViewModel
import com.raczu.skincareapp.views.ProductEditViewModel
import com.raczu.skincareapp.views.ProductsViewModel
import com.raczu.skincareapp.views.RoutineAddViewModel
import com.raczu.skincareapp.views.RoutineDetailsViewModel
import com.raczu.skincareapp.views.RoutineNotificationViewModel
import com.raczu.skincareapp.views.RoutineViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
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
    }
}

fun CreationExtras.skinCareApplication(): SkinCareApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SkinCareApplication)
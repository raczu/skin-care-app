package com.raczu.skincareapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.raczu.skincareapp.views.ProductAddViewModel
import com.raczu.skincareapp.views.ProductEditViewModel
import com.raczu.skincareapp.views.ProductsViewModel

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
    }
}

fun CreationExtras.skinCareApplication(): SkinCareApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SkinCareApplication)
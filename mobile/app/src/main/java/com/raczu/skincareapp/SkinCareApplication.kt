package com.raczu.skincareapp

import android.app.Application
import com.raczu.skincareapp.di.AppContainer

class SkinCareApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
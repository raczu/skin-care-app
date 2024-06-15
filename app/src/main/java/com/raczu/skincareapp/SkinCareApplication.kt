package com.raczu.skincareapp

import android.app.Application

class SkinCareApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
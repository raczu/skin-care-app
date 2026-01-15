package com.raczu.skincareapp.data.remote.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.raczu.skincareapp.SkinCareApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppMessagingService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val container = (application as SkinCareApplication).container

        serviceScope.launch {
            val isLoggedIn = container.tokenManager.isUserLoggedIn.first()
            if (isLoggedIn) sendRegistrationToServer(token)
        }
    }

    private fun sendRegistrationToServer(token: String) {
        val container = (application as SkinCareApplication).container

        serviceScope.launch {
            container.deviceTokenRepository.saveCurrentToken(token)
                .onFailure {
                    Log.d("AppMessagingService", "Failed to save device token: ${it.message}")
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
package com.raczu.skincareapp.data.remote.service

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.raczu.skincareapp.MainActivity
import com.raczu.skincareapp.R
import com.raczu.skincareapp.SkinCareApplication
import com.raczu.skincareapp.data.local.notifications.AppNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.getValue

class AppMessagingService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val appNotificationManager by lazy { AppNotificationManager(applicationContext) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val container = (application as SkinCareApplication).container

        serviceScope.launch {
            val isLoggedIn = container.tokenManager.isUserLoggedIn.first()
            if (isLoggedIn) sendRegistrationToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            appNotificationManager.showNotification(it.title, it.body)
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
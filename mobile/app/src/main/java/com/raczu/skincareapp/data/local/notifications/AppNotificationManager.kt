package com.raczu.skincareapp.data.local.notifications

import android.app.NotificationChannel
import android.content.Context
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.raczu.skincareapp.R
import com.raczu.skincareapp.MainActivity

class AppNotificationManager(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String?, body: String?) {
        createNotificationChannel()
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationId = title.hashCode()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channelId = context.getString(R.string.default_notification_channel_id)
            val name = context.getString(R.string.default_notification_channel_name)
            val descriptionText = context.getString(R.string.default_notification_channel_description)

            if (notificationManager.getNotificationChannel(channelId) == null) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
package com.codr.movieshazam

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class AppNotificationChannel(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    // Function to start observing the count and updating the notification
    fun bringToLife() {
        showNotification()
    }

    // Function to show the notification
    @SuppressLint("SuspiciousIndentation")
    private fun showNotification() {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            4,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Find a Movie")
            .setContentText("Recording") // Display the current count value
            .setContentIntent(activityPendingIntent)
            .setOnlyAlertOnce(true) // This prevents the sound from playing on every update
            notification.addAction(
                R.drawable.ic_launcher_background,
                "Reset",
                activityPendingIntent
            )

        notificationManager.notify(7, notification.build())
    }


    companion object {
        const val CHANNEL_ID = "app_notification_channel"
    }
}

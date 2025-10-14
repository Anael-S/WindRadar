package com.anael.samples.apps.windradar.utilities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anael.samples.apps.windradar.utilities.weather.WeatherCalculator

object NotificationUtils {
    fun showWindAlertNotification(context: Context, alertResult: WeatherCalculator.AlertResult) {
        val channelId = "wind_alert_channel"
        val channelName = "Wind Alerts"
        val notificationId = alertResult.hashCode()  // Unique ID per alert time

        // Create notification channel if necessary
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notifications for strong wind alerts"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Strong Wind Alert")
            .setContentText("Wind or gusts: ${alertResult.windSpeed} km/h at ${alertResult.alertTime} for ${alertResult.hoursAboveThreshold} hours")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                        context,
                        "Notification permission required to show alerts.",
                        Toast.LENGTH_LONG
                ).show()
                return
            }
            notify(notificationId, builder.build())
        }
    }
}
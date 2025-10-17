package com.anael.samples.apps.windradar.utilities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.anael.samples.apps.windradar.utilities.weather.WeatherCalculator

object NotificationUtils {

    private const val CHANNEL_ID = "wind_alert_channel"
    private const val CHANNEL_NAME = "Wind Alerts"

    fun showWindAlertNotification(context: Context, alertResult: WeatherCalculator.AlertResult) {
        // 1) Check permission (Android 13+). Do NOT try to request it here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                // No UI from worker. Just log and return.
                android.util.Log.w("AlertCheckWorker", "Notification permission not granted.")
                return
            }
        }

        // 2) Ensure notification channel exists (safe to call repeatedly)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for strong wind alerts"
        }
        nm.createNotificationChannel(channel)

        // 3) Build the notification (consider adding a PendingIntent to open the app)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Strong Wind Alert")
            .setContentText(
                "Wind or gusts: ${alertResult.windSpeed} km/h at ${alertResult.alertTime} " +
                        "for ${alertResult.hoursAboveThreshold} hours"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 4) Show it
        NotificationManagerCompat.from(context)
            .notify(alertResult.hashCode(), builder.build())
    }
}

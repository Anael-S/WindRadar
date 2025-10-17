package com.anael.samples.apps.windradar.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anael.samples.apps.windradar.R
import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.WindRepository
import com.anael.samples.apps.windradar.data.local.AlertRepository
import com.anael.samples.apps.windradar.utilities.weather.WeatherCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AlertCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val alertsRepo: AlertRepository,
    private val cityRepo: CitySelectionRepository,
    private val weatherRepo: WindRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        android.util.Log.d("AlertCheckWorker", "Starting doWork()")
        val city = cityRepo.selectedCity.first()
            ?: //0 - No city selected — nothing to check
            return Result.success()

        // 1) Load all enabled alerts
        val alerts = alertsRepo.getEnabledAlerts()

        // 2) Get the weather once (or per location if needed)
        val hourlyWeatherData = weatherRepo.getHourlyWindDataPrevision(
            latitude = city.latitude,
            longitude = city.longitude,
            timezone = city.timezone?: ""
        ).first()

        // 3) Check each alert
        alerts.forEach { alert ->
            val result = WeatherCalculator.calculateIfAlertIsNecessary(
                context = applicationContext,
                hourlyWeatherData = hourlyWeatherData.hourlyWeatherData,
                windThreshold = alert.windMin,
                gustThreshold = alert.gustMin,
                startHour = alert.startHour,
                endHour = alert.endHour,
                maxDayForward = 1
                // directions = null, dirStart = null, dirEnd = null
            )
            android.util.Log.d("AlertCheckWorker", "Alert check done, alert needed? " + result.shouldAlert)
            if (result.shouldAlert) {
                postNotification(
                    title = applicationContext.getString(R.string.alert_notification_title),
                    message = applicationContext.getString(
                        R.string.wind_alert_message_format,
                        result.windSpeed ?: "—",
                        result.alertTime ?: "—",
                        result.hoursAboveThreshold
                    )
                )
            }
        }

        android.util.Log.d("AlertCheckWorker", "Finished doWork()")
        // 4) Tell WorkManager we succeeded (even if no alerts fired)
        return Result.success()
    }

    private fun postNotification(title: String, message: String) {
        val channelId = "wind_alerts"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId, applicationContext.getString(R.string.alerts),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        nm.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .build()

        nm.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}

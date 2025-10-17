package com.anael.samples.apps.windradar.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.WindRepository
import com.anael.samples.apps.windradar.data.local.AlertRepository
import com.anael.samples.apps.windradar.utilities.NotificationUtils
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
            )
            if (result.shouldAlert) {
                NotificationUtils.showWindAlertNotification(applicationContext, result)
            }
        }

        // 4) Tell WorkManager we succeeded (even if no alerts fired)
        return Result.success()
    }

}

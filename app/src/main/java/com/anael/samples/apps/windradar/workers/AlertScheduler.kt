package com.anael.samples.apps.windradar.workers

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object AlertScheduler {
    private const val UNIQUE_NAME = "AlertHourlyCheck"

    fun scheduleHourly(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED) // if weather needs network
            .build()

        val request = PeriodicWorkRequestBuilder<AlertCheckWorker>(
            1, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.MINUTES) // optional stagger after install/app open
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // replace if it's already scheduled
            request
        )
    }
}

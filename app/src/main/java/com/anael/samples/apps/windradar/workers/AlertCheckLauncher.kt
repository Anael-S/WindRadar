package com.anael.samples.apps.windradar.workers

import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertCheckLauncher @Inject constructor(
    private val workManager: WorkManager
) {
    private val UNIQUE_NOW = "AlertCheckNow"

    fun runNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<AlertCheckWorker>()
            .setConstraints(constraints)
            .addTag(UNIQUE_NOW)
            .build()



        workManager.enqueueUniqueWork(
            UNIQUE_NOW,
            ExistingWorkPolicy.REPLACE,
            request
        )

        // Debug-only helper
//        val workId = request.id  // UUID of this request
//        workManager.getWorkInfoById(workId).get().also { info ->
//            android.util.Log.d("AlertCheckWorker", "state=${info.state} tags=${info.tags} output=${info.outputData}")
//        }
    }
}

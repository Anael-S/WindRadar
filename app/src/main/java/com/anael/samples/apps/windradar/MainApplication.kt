package com.anael.samples.apps.windradar

import androidx.hilt.work.HiltWorkerFactory
import com.anael.samples.apps.windradar.workers.AlertScheduler
import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

  @Inject lateinit var workerFactory: HiltWorkerFactory

  override fun onCreate() {
    super.onCreate()
    // 1) Force WorkManager to use our configuration (with HiltWorkerFactory) - otherwise the scheduler below might pick the default worker and not ours!
    WorkManager.initialize(this, workManagerConfiguration)

    // 2) Now it's safe to schedule things
    AlertScheduler.scheduleHourly(WorkManager.getInstance(this))
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .setMinimumLoggingLevel(
        if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR
      )
      .build()
}

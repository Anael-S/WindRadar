package com.anael.samples.apps.windradar

import android.app.Application
import androidx.work.Configuration
import com.google.samples.apps.sunflower.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
      .build()
}

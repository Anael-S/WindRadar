package com.anael.samples.apps.windradar.utilities

import android.content.Context

object ThresholdPrefs {
    private const val PREF_NAME = "WindThresholdPrefs"
    private const val KEY_WIND_THRESHOLD = "wind_threshold"
    private const val KEY_GUST_THRESHOLD = "gust_threshold"

    fun saveThresholds(context: Context, windThreshold: Int, gustThreshold: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_WIND_THRESHOLD, windThreshold)
            .putInt(KEY_GUST_THRESHOLD, gustThreshold)
            .apply()
    }

    fun loadWindThreshold(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_WIND_THRESHOLD, 40)
    }

    fun loadGustThreshold(context: Context): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_GUST_THRESHOLD, 40)
    }

    fun loadMaxDayForward(context: Context): Int {
        val prefs = context.getSharedPreferences("threshold_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("max_day_forward", 1)
    }

    fun saveMaxDayForward(context: Context, value: Int) {
        val prefs = context.getSharedPreferences("threshold_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("max_day_forward", value).apply()
    }

}

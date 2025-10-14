package com.anael.samples.apps.windradar.utilities

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
    private const val REQUEST_NOTIFICATION_PERMISSION = 1001

    /**
     * Request POST_NOTIFICATIONS and exact alarm permission if needed
     */
    fun requestNecessaryPermissions(activity: ComponentActivity) {
        requestNotificationPermissionIfNeeded(activity)
    }

    /**
     * Request POST_NOTIFICATIONS on Android 13+ if not granted
     */
    private fun requestNotificationPermissionIfNeeded(activity: ComponentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            val granted =
                ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    fun needsExactAlarmPermission(context: Context): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        return !alarmManager?.canScheduleExactAlarms()!!
    }

    /**
     * Handle permission result for POST_NOTIFICATIONS
     */
    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ): Boolean {
        return if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionUtils", "Notification permission granted")
                true
            } else {
                Log.w("PermissionUtils", "Notification permission denied")
                false
            }
        } else {
            false
        }
    }
}

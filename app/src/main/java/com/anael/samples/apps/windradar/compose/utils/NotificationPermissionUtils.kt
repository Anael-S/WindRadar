package com.anael.samples.apps.windradar.compose.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

fun ensureNotificationPermission(
    context: Context,
    activity: Activity?,
    launcher: ActivityResultLauncher<String>,
    onGranted: () -> Unit,
    onDeniedImmediate: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        onGranted()
        return
    }

    val granted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    if (granted) {
        onGranted()
        return
    }

    if (activity != null) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        onDeniedImmediate()
    }
}
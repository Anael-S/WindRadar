package com.anael.samples.apps.windradar

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.anael.samples.apps.windradar.navigation.AppNavHost

@Composable
fun WindRadarApp() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface {
            AppNavHost(navController = navController)
        }
    }
}

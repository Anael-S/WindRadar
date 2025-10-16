package com.anael.samples.apps.windradar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.anael.samples.apps.windradar.compose.WeatherScreen
import com.anael.samples.apps.windradar.compose.alert.AlertSummary
import com.anael.samples.apps.windradar.compose.alert.model.Alert

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen(
                onOpenAlerts = { navController.navigate("alerts") }
            )
        }

        composable("alerts") {
            val sampleAlerts = listOf(
                Alert("1", "Strong wind alert", true, "Wind ≥ 25 km/h • 06:00–18:00"),
                Alert("2", "Morning breeze", false, "Wind ≥ 15 km/h • 08:00–12:00")
            )
            AlertSummary(
                alerts = sampleAlerts,
                onToggle = { _, _ -> },
                onEdit = {},
                onDelete = {},
                onAdd = { /* could open bottom sheet again */ }
            )
        }
    }
}

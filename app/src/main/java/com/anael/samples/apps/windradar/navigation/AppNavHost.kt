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
            AlertSummary(
                onAdd = {
                    //TODO:
                    // Decide what Add should do in alerts screen:
                    // - open a bottom sheet in the SAME screen (preferred), OR
                    // - navigate to a "create" route if you have one
                    // navController.navigate("alerts/create")
                }
            )
        }
    }
}

package com.anael.samples.apps.windradar.compose.weather

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anael.samples.apps.windradar.viewmodels.ForecastMode

@Composable
fun ForecastModeToggle(
    selected: ForecastMode,
    onSelected: (ForecastMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(ForecastMode.Daily, ForecastMode.Hourly)
    val selectedIndex = items.indexOf(selected).coerceAtLeast(0)

    androidx.compose.material3.TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            androidx.compose.material3.Tab(
                selected = index == selectedIndex,
                onClick = { onSelected(item) },
                text = { androidx.compose.material3.Text(if (item == ForecastMode.Daily) "Daily" else "Hourly") }
            )
        }
    }
}

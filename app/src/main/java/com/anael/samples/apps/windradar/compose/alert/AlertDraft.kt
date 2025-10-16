package com.anael.samples.apps.windradar.compose.alert

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AlertDraft(
    val name: String,
    val windMin: Float,
    val gustMin: Float,
    val dirStart: Int?,
    val dirEnd: Int?,
    val startHour: Int,
    val endHour: Int,
    val enabled: Boolean = true
)

@Composable
fun AlertQuickForm(
    onCancel: () -> Unit,
    onSave: (AlertDraft) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var wind by remember { mutableStateOf(15f) }
    var gust by remember { mutableStateOf(25f) }
    var dirEnabled by remember { mutableStateOf(false) }
    var dirStart by remember { mutableStateOf(0f) }
    var dirEnd by remember { mutableStateOf(360f) }
    var startHour by remember { mutableStateOf(6f) }
    var endHour by remember { mutableStateOf(18f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create alert", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LabeledSlider(
            label = "Wind ≥ ${"%.0f".format(wind)} km/h",
            value = wind,
            onValueChange = { wind = it },
            valueRange = 0f..60f
        )

        LabeledSlider(
            label = "Gusts ≥ ${"%.0f".format(gust)} km/h",
            value = gust,
            onValueChange = { gust = it },
            valueRange = 0f..100f
        )

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Switch(checked = dirEnabled, onCheckedChange = { dirEnabled = it })
            Text("Limit wind direction (°)")
        }
        if (dirEnabled) {
            RangeRow(
                labelStart = "From",
                startValue = dirStart,
                onStartChange = { dirStart = it },
                labelEnd = "To",
                endValue = dirEnd,
                onEndChange = { dirEnd = it },
                range = 0f..360f,
                step = 1
            )
        }

        Text("Time window")
        RangeRow(
            labelStart = "Start",
            startValue = startHour,
            onStartChange = { startHour = it },
            labelEnd = "End",
            endValue = endHour,
            onEndChange = { endHour = it },
            range = 0f..24f,
            step = 1,
            valueFormatter = { "${it.toInt()}:00" }
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Button(
                onClick = {
                    onSave(
                        AlertDraft(
                            name = name.trim(),
                            windMin = wind,
                            gustMin = gust,
                            dirStart = if (dirEnabled) dirStart.toInt() else null,
                            dirEnd = if (dirEnabled) dirEnd.toInt() else null,
                            startHour = startHour.toInt(),
                            endHour = endHour.toInt(),
                        )
                    )
                }
            ) { Text("Save") }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}

@Composable
private fun RangeRow(
    labelStart: String,
    startValue: Float,
    onStartChange: (Float) -> Unit,
    labelEnd: String,
    endValue: Float,
    onEndChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    step: Int = 1,
    valueFormatter: (Float) -> String = { it.toInt().toString() }
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("$labelStart: ${valueFormatter(startValue)}")
                Slider(
                    value = startValue,
                    onValueChange = { onStartChange(it.coerceIn(range)) },
                    valueRange = range,
                    steps = ((range.endInclusive - range.start) / step - 1).toInt().coerceAtLeast(0)
                )
            }
            Column(Modifier.weight(1f)) {
                Text("$labelEnd: ${valueFormatter(endValue)}")
                Slider(
                    value = endValue,
                    onValueChange = { onEndChange(it.coerceIn(range)) },
                    valueRange = range,
                    steps = ((range.endInclusive - range.start) / step - 1).toInt().coerceAtLeast(0)
                )
            }
        }
    }
}

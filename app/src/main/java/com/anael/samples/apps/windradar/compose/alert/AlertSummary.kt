package com.anael.samples.apps.windradar.compose.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAlert
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class AlertSummary(
    val id: String,
    val title: String,
    val enabled: Boolean,
    val summary: String,   // e.g., "Wind ≥ 25 km/h • 06:00–18:00 • Dir 180–240°"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsRoute(
    alerts: List<AlertSummary>,
    onToggle: (id: String, enabled: Boolean) -> Unit,
    onEdit: (id: String) -> Unit,
    onDelete: (id: String) -> Unit,
    onAdd: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Alerts") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                text = { Text("Add alert") },
                icon = { Icon(Icons.Rounded.AddAlert, contentDescription = null) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alerts, key = { it.id }) { a ->
                AlertCard(a, onToggle, onEdit, onDelete)
            }
        }
    }
}

@Composable
private fun AlertCard(
    a: AlertSummary,
    onToggle: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Card {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(a.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(a.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Switch(checked = a.enabled, onCheckedChange = { onToggle(a.id, it) })
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = { onEdit(a.id) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { onDelete(a.id) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

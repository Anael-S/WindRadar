package com.anael.samples.apps.windradar.compose.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAlert
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anael.samples.apps.windradar.R
import com.anael.samples.apps.windradar.compose.alert.model.Alert
import com.anael.samples.apps.windradar.viewmodels.AlertsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertSummary(
    alertsViewModel: AlertsViewModel = hiltViewModel(),
) {
    val alerts by alertsViewModel.alertsUi.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<Alert?>(null) }

    // this is the single source of truth for both "create" and "edit", if necessary for later > right now it is not
    var editingDraft by remember { mutableStateOf<AlertDraft?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.alerts)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingDraft = defaultDraft()
                },
                text = { Text(stringResource(R.string.add_alert)) },
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
            items(items = alerts, key = { it.id }) { alert ->
                AlertCard(
                    alert = alert,
                    onToggle = { id, enabled -> alertsViewModel.enableAlert(id, enabled) },
                    onDelete = { pendingDelete = alert } // open confirm dialog
                )
            }
        }
    }

    if (pendingDelete != null) {
        ConfirmDeleteDialog(
            onDismiss = { pendingDelete = null },
            onConfirm = {
                alertsViewModel.deleteAlert(pendingDelete!!.id)
                pendingDelete = null
            }
        )
    }

    // bottom sheet for both create & edit
    if (editingDraft != null) {
        EditAlertSheet(
            initial = editingDraft!!,
            onDismiss = { editingDraft = null },
            onSave = { draft ->
                alertsViewModel.createOrUpdateAlert(draft)
                editingDraft = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAlertSheet(
    initial: AlertDraft,
    onDismiss: () -> Unit,
    onSave: (AlertDraft) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        AlertQuickForm(
            onCancel = onDismiss,
            onSave = onSave
        )
        // If you want the form prefilled when editing, make AlertQuickForm accept `initial`
        // and initialize its state from it; for create just pass `defaultDraft()`.
    }
}


fun defaultDraft() = AlertDraft(
    name = "",
    windMin = 15f,
    gustMin = 25f,
    dirStart = null,
    dirEnd = null,
    startHour = 6,
    endHour = 18,
    enabled = true
)

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete)) },
        text = { Text(stringResource(R.string.confirm_delete_alert)) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.delete)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

@Composable
private fun AlertCard(
    alert: Alert,
    onToggle: (String, Boolean) -> Unit,
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
                Text(alert.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    alert.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Switch(checked = alert.enabled, onCheckedChange = { onToggle(alert.id, it) })
                IconButton(onClick = { onDelete(alert.id) }) {
                    Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        }
    }
}

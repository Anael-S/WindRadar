package com.anael.samples.apps.windradar.compose

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAlert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anael.samples.apps.windradar.R
import com.anael.samples.apps.windradar.compose.alert.AlertDraft
import com.anael.samples.apps.windradar.compose.alert.AlertQuickForm
import com.anael.samples.apps.windradar.compose.settings.SuggestionAddressTextField
import com.anael.samples.apps.windradar.compose.utils.ensureNotificationPermission
import com.anael.samples.apps.windradar.compose.weather.WeatherContent
import com.anael.samples.apps.windradar.viewmodels.AlertsViewModel
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel
import com.anael.samples.apps.windradar.viewmodels.SelectedCityViewModel
import com.anael.samples.apps.windradar.viewmodels.WeatherViewModel
import findActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    citySuggestionViewModel: CitySuggestionViewModel = hiltViewModel(),
    selectedCityViewModel: SelectedCityViewModel = hiltViewModel(),
    alertsViewModel: AlertsViewModel = hiltViewModel(),
    onOpenAlerts: () -> Unit = {}
) {
    val savedCity by selectedCityViewModel.city.collectAsStateWithLifecycle()
    val dailyState by viewModel.dailyUi.collectAsStateWithLifecycle()
    val hourlyState by viewModel.hourlyUi.collectAsStateWithLifecycle()

    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() } // helper below
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Launcher to request POST_NOTIFICATIONS
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        // This callback runs after the system dialog
        if (!granted) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Notifications are off — you can enable them in Settings to get wind alerts."
                )
            }
        }
    }

    LaunchedEffect(savedCity) {
        savedCity?.let {
            val display = listOfNotNull(it.name).joinToString(", ")
            citySuggestionViewModel.onQueryChanged(display)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        // Gate the save behind the permission (Android 13+)
                        ensureNotificationPermission(
                            context = context,
                            activity = activity,
                            launcher = notifPermissionLauncher,
                            onGranted = {
                                onOpenAlerts()
                            },
                            onDeniedImmediate = {
                                // User already denied before (no system dialog shown). Nudge with snackbar.
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Turn on notifications in Settings to receive wind alerts."
                                    )
                                }
                            }
                        )

                    }) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Manage alerts")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Gate the save behind the permission (Android 13+)
                ensureNotificationPermission(
                    context = context,
                    activity = activity,
                    launcher = notifPermissionLauncher,
                    onGranted = {
                        showCreateSheet = true
                    },
                    onDeniedImmediate = {
                        // User already denied before (no system dialog shown). Nudge with snackbar.
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Turn on notifications in Settings to receive wind alerts."
                            )
                        }
                        showCreateSheet = false
                    }
                )
            }) {
                Icon(Icons.Rounded.AddAlert, contentDescription = "Create alert")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SuggestionAddressTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                viewModel = citySuggestionViewModel,
                onPersistSelected = { selectedCityViewModel.onCityChosen(it.toCitySelection()) }
            )

            WeatherContent(
                dailyState = dailyState,
                hourlyState = hourlyState,
                onPullToRefresh = { viewModel.refreshData() }
            )
        }
    }

    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            AlertQuickForm(
                onCancel = { showCreateSheet = false },
                onSave = { draft: AlertDraft ->
                    alertsViewModel.createOrUpdateAlertAndRunCheck(draft)
                    showCreateSheet = false
                }
            )
        }
    }
}

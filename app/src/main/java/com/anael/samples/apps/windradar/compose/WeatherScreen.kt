package com.anael.samples.apps.windradar.compose

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anael.samples.apps.windradar.compose.alert.AlertDraft
import com.anael.samples.apps.windradar.compose.alert.AlertQuickForm
import com.anael.samples.apps.windradar.compose.settings.SuggestionAddressTextField
import com.anael.samples.apps.windradar.compose.weather.WeatherContent
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel
import com.anael.samples.apps.windradar.viewmodels.SelectedCityViewModel
import com.anael.samples.apps.windradar.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel(),
    citySuggestionViewModel: CitySuggestionViewModel = hiltViewModel(),
    selectedCityVm: SelectedCityViewModel = hiltViewModel(),
    onOpenAlerts: () -> Unit = {} // hook to navigate to AlertsRoute()
) {
    val savedCity by selectedCityVm.city.collectAsStateWithLifecycle()
    val dailyState by viewModel.dailyUi.collectAsStateWithLifecycle()
    val hourlyState by viewModel.hourlyUi.collectAsStateWithLifecycle()

    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(savedCity) {
        savedCity?.let {
            val display = listOfNotNull(it.name).joinToString(", ")
            citySuggestionViewModel.onQueryChanged(display)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "WindRadar") }, // move to stringResource if you have it
                actions = {
                    IconButton(onClick = onOpenAlerts) {
                        Icon(Icons.Rounded.Notifications, contentDescription = "Manage alerts")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateSheet = true }) {
                Icon(Icons.Rounded.AddAlert, contentDescription = "Create alert")
            }
        }
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
                onPersistSelected = { selectedCityVm.onCityChosen(it.toCitySelection()) }
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
                    // TODO: write to the repository / automation here
                    // viewModel.createAlert(draft)
                    showCreateSheet = false
                }
            )
        }
    }
}

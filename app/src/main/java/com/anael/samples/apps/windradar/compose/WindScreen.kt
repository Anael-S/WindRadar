package com.anael.samples.apps.windradar.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anael.samples.apps.windradar.compose.settings.SuggestionAddressTextField
import com.anael.samples.apps.windradar.compose.weather.ForecastModeToggle
import com.anael.samples.apps.windradar.compose.weather.WeatherContent
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel
import com.anael.samples.apps.windradar.viewmodels.SelectedCityViewModel
import com.anael.samples.apps.windradar.viewmodels.WindViewModel

@Composable
fun WindScreen(
    viewModel: WindViewModel = hiltViewModel(),
    citySuggestionViewModel: CitySuggestionViewModel = hiltViewModel(),
    selectedCityVm: SelectedCityViewModel = hiltViewModel(),
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val savedCity by selectedCityVm.city.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LaunchedEffect(savedCity) {
                savedCity?.let {
                    val display = listOfNotNull(it.name).joinToString(", ")
                    citySuggestionViewModel.onQueryChanged(display)
                }
            }

            SuggestionAddressTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                viewModel = citySuggestionViewModel,
                onPersistSelected = { selectedCityVm.onCityChosen(it.toCitySelection()) }
            )

            ForecastModeToggle(
                selected = mode,
                onSelected = viewModel::setMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            WeatherContent(
                weatherState = weatherState,
                onPullToRefresh = viewModel::refreshData,
            )
        }
    }
}
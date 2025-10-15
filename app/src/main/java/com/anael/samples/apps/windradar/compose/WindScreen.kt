package com.anael.samples.apps.windradar.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anael.samples.apps.windradar.compose.settings.SuggestionAddressTextField
import com.anael.samples.apps.windradar.compose.weather.WeatherContent
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel
import com.anael.samples.apps.windradar.viewmodels.WindViewModel

@Composable
fun WindScreen(
    viewModel: WindViewModel = hiltViewModel(),
    citySuggestionViewModel: CitySuggestionViewModel = hiltViewModel(),
) {
    val weatherState by viewModel.weatherState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SuggestionAddressTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                viewModel = citySuggestionViewModel
            )

            WeatherContent(
                weatherState = weatherState,
                onPullToRefresh = viewModel::refreshData,
            )
        }
    }
}
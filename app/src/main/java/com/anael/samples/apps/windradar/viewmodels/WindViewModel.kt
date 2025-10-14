/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.WindData
import com.anael.samples.apps.windradar.data.WindRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WindViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WindRepository
) : ViewModel() {

    private var timezone: String? = savedStateHandle["timezone"]
    private var lattitude: Double? = savedStateHandle["lattitude"]
    private var longitude: Double? = savedStateHandle["lattitude"]


    private val _windDataPrevisions = MutableStateFlow<WindData?>(null)
    val windDataPrevisions: Flow<WindData> get() = _windDataPrevisions.filterNotNull()

    init {
        refreshData()
    }


    fun refreshData() {
        viewModelScope.launch {
            try {

                //TODO: remove this, MOCKUP for now
                val lat = lattitude ?: 52.03634
                val lon = longitude ?: 4.32501
                val tz = timezone ?: "Europe/Amsterdam"
                _windDataPrevisions.value = repository.getWindDataPrevision(latitude = lat, longitude = lon, timezone = tz).first()

//                _windDataPrevisions.value = repository.getWindDataPrevision(latitude = lattitude!!, longitude = longitude!!, timezone = timezone ?: "").first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
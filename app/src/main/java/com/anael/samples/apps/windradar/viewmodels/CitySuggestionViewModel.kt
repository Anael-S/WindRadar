package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.CitySuggestionRepository
import com.anael.samples.apps.windradar.data.GeoResultData
import com.anael.samples.apps.windradar.data.WeatherData
import com.anael.samples.apps.windradar.data.WeatherWithUnitData
import com.anael.samples.apps.windradar.data.WindRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitySuggestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CitySuggestionRepository
) : ViewModel() {

        private val _query = MutableStateFlow("")  // holds current typed text
        fun onQueryChanged(new: String) {
            _query.value = new
        }

    val suggestions: StateFlow<List<GeoResultData>> = _query
        .debounce(300)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.getSuggestions(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


}
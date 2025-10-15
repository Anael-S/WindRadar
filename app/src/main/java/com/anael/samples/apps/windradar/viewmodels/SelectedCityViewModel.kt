package com.anael.samples.apps.windradar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anael.samples.apps.windradar.data.model.CitySelection
import com.anael.samples.apps.windradar.domain.ObserveSelectedCity
import com.anael.samples.apps.windradar.domain.SaveSelectedCity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedCityViewModel @Inject constructor(
  private val saveSelectedCity: SaveSelectedCity,
  observeSelectedCity: ObserveSelectedCity
) : ViewModel() {

  val city: StateFlow<CitySelection?> =
    observeSelectedCity()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  fun onCityChosen(city: CitySelection) {
    viewModelScope.launch { saveSelectedCity(city) }
  }
}
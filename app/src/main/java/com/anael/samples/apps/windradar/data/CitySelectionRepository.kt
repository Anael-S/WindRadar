package com.anael.samples.apps.windradar.data
import com.anael.samples.apps.windradar.data.model.CitySelection
import kotlinx.coroutines.flow.Flow

interface CitySelectionRepository {
  val selectedCity: Flow<CitySelection?>
  suspend fun saveCity(city: CitySelection)
}
package com.anael.samples.apps.windradar.domain

import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.model.CitySelection
import javax.inject.Inject

class SaveSelectedCity @Inject constructor(
  private val repo: CitySelectionRepository
) { suspend operator fun invoke(city: CitySelection) = repo.saveCity(city) }
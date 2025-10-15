package com.anael.samples.apps.windradar.domain

import com.anael.samples.apps.windradar.data.CitySelectionRepository
import com.anael.samples.apps.windradar.data.model.CitySelection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedCity @Inject constructor(
  private val repo: CitySelectionRepository
) { operator fun invoke(): Flow<CitySelection?> = repo.selectedCity }
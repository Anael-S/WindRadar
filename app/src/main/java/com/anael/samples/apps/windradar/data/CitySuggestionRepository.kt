package com.anael.samples.apps.windradar.data

import com.anael.samples.apps.windradar.api.CitySuggestionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CitySuggestionRepository @Inject constructor(private val citySuggestionService: CitySuggestionService) {

    fun getSuggestions(query: String): Flow<List<GeoResultData>> = flow {
        emit(citySuggestionService.fetchSuggestions(name = query).citySuggested)
    }
}

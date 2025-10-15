package com.anael.samples.apps.windradar.compose.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.anael.samples.apps.windradar.data.GeoResultData
import com.anael.samples.apps.windradar.viewmodels.CitySuggestionViewModel

@Composable
fun SuggestionAddressTextField(
    modifier: Modifier = Modifier,
    viewModel: CitySuggestionViewModel,
) {
    val citySuggestions by viewModel.suggestions.collectAsState()
    val cityQuery by viewModel.query.collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier) {
        AddressInputField(
            text = cityQuery,
            onTextChange = { new ->
                viewModel.onQueryChanged(new)
                expanded = true
            }
        )

        SuggestionsDropdown(
            suggestions = citySuggestions,
            expanded = expanded,
            onSelect = { suggestion ->
                val fullAddress = listOfNotNull(
                    suggestion.name,
                    suggestion.admin1,
                    suggestion.country
                ).joinToString(", ")

                viewModel.onQueryChanged(fullAddress)
                expanded = false
                focusManager.clearFocus()
            }
        )
    }
}


@Composable
fun AddressInputField(
    text: String,
    onTextChange: (String) -> Unit,
) {
    // keep a local TextFieldValue to preserve composition/selection
    var localValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text))
    }

    // sync when external text changes (ViewModel -> UI)
    LaunchedEffect(text) {
        if (text != localValue.text) {
            // preserve selection if possible
            localValue = localValue.copy(text = text)
        }
    }

    OutlinedTextField(
        value = localValue,
        onValueChange = { newValue ->
            localValue = newValue
            onTextChange(newValue.text) // push String to ViewModel
        },
        label = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { /* no extra re-requesting */ },
        singleLine = true,
    )
}


@Composable
fun SuggestionsDropdown(
    suggestions: List<GeoResultData>?,
    expanded: Boolean,
    onSelect: (GeoResultData) -> Unit,
) {
    val list = suggestions ?: emptyList()
    DropdownMenu(
        expanded = expanded && list.isNotEmpty(),
        onDismissRequest = {},
        modifier = Modifier.fillMaxWidth(),
        properties = PopupProperties(focusable = false)
    ) {
        list.forEach { suggestion ->
            val displayText = listOfNotNull(
                suggestion.name,
                suggestion.admin1,
                suggestion.country
            ).joinToString(", ")

            Text(
                text = displayText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onSelect(suggestion) }
            )
        }
    }
}




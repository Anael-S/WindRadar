package com.anael.samples.apps.windradar.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anael.samples.apps.windradar.data.WindData
import com.google.samples.apps.sunflower.R
import com.anael.samples.apps.windradar.viewmodels.WindViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun WindScreen(viewModel: WindViewModel = hiltViewModel()) {
    WindScreen(
            windData = viewModel.windDataPrevisions,
            onPullToRefresh = viewModel::refreshData
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WindScreen(
    windData: Flow<WindData>,
    onPullToRefresh: () -> Unit
) {
    val data = windData.collectAsState(initial = null)
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold { padding ->
        if (pullToRefreshState.isRefreshing) {
            onPullToRefresh()
        }

        Box(
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            data.value?.let { wind ->
                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                                all = dimensionResource(id = R.dimen.card_side_margin)
                        )
                ) {
                    items(wind.time.size) { index ->
                        val time = wind.time[index]
                        val speed = wind.windSpeeds[index]
                        val gust = wind.windGusts[index]

                        WindItem(
                                time = time,
                                speed = speed,
                                gust = gust
                        )
                    }
                }
            }

            PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullToRefreshState
            )
        }
    }
}

@Composable
fun WindItem(time: String, speed: Double, gust: Double) {
    androidx.compose.material3.Card(
            modifier = Modifier.padding(8.dp)
    ) {
        androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp)
        ) {
            androidx.compose.material3.Text(text = time)
            androidx.compose.material3.Text(text = "Speed: $speed m/s")
            androidx.compose.material3.Text(text = "Gust: $gust m/s")
        }
    }
}

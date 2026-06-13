package com.cardosogui.citybikesexplorer.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.stations.StationCard
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import com.cardosogui.citybikesexplorer.ui.EmptyState
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun FavoritesRoute(
    modifier: Modifier = Modifier,
    onStationClick: (stationId: String) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    FavoritesScreen(
        modifier = modifier,
        state = state,
        onStationClick = onStationClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    state: FavoritesUiState,
    onStationClick: (stationId: String) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorites_title)) },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
    ) { innerPadding ->
        when (state) {
            is FavoritesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is FavoritesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
                }
            }

            is FavoritesUiState.Content -> {
                if (state.stations.isEmpty()) {
                    EmptyState(
                        modifier = Modifier.padding(innerPadding),
                        iconRes = R.drawable.favorite_border_24dp,
                        title = stringResource(R.string.no_favorites),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(White),
                    ) {
                        items(state.stations, key = { it.id }) { station ->
                            StationCard(stationState = station, onClick = { onStationClick(station.id) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun FavoritesEmptyPreview() {
    FavoritesScreen(state = FavoritesUiState.Content(emptyList()))
}

@Composable
@Preview
private fun FavoritesContentPreview() {
    FavoritesScreen(
        state = FavoritesUiState.Content(
            listOf(
                StationsViewModel.StationState(
                    id = "1",
                    name = "Harbour Quay",
                    freeBikes = 5,
                    emptySlots = 10,
                    latitude = 0.0,
                    longitude = 0.0,
                    address = "123 Main St",
                    lastUpdated = "2026-06-13T08:45:00Z",
                    distanceKm = 0.25,
                    minWalk = "3 min walk",
                    imageLink = "",
                ),
            ),
        ),
    )
}

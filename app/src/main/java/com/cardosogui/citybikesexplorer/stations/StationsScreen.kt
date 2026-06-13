package com.cardosogui.citybikesexplorer.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.ui.EmptyState
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.Orange
import com.cardosogui.citybikesexplorer.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    modifier: Modifier = Modifier,
    state: StationsUiState,
    onClick: (stationId: String) -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterChange: (StationFilter) -> Unit,
    retry: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stations_title)) },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
    ) { innerPadding ->
        when (state) {
            is StationsUiState.Loading -> {
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

            is StationsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
                    Button(modifier = Modifier.padding(top = 16.dp), onClick = retry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

            is StationsUiState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                ) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = onSearchChange,
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.search_24dp),
                                contentDescription = null,
                            )
                        },
                        placeholder = { Text(stringResource(R.string.search_stations_hint)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )

                    StationFilterPills(
                        selected = state.filter,
                        onSelect = onFilterChange,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    if (state.stations.isEmpty()) {
                        EmptyState(
                            iconRes = R.drawable.search_24dp,
                            title = stringResource(R.string.no_stations_found),
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.stations, key = { it.id }) { station ->
                                StationCard(stationState = station, onClick = { onClick(station.id) })
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationFilterPills(
    selected: StationFilter,
    onSelect: (StationFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        StationFilter.ALL to stringResource(R.string.filter_all),
        StationFilter.AVAILABLE to stringResource(R.string.filter_available),
        StationFilter.UNAVAILABLE to stringResource(R.string.filter_unavailable),
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (filter, label) ->
            FilterChip(
                selected = selected == filter,
                onClick = { onSelect(filter) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenActive,
                    selectedLabelColor = White,
                ),
            )
        }
    }
}

@Composable
fun StationCard(
    stationState: StationsViewModel.StationState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val color = if (stationState.freeBikes == 0) {
                Color.Red
            } else if (stationState.freeBikes / (stationState.freeBikes + stationState.emptySlots).toFloat() < 0.3f) {
                Orange
            } else {
                GreenActive
            }

            Icon(
                modifier = Modifier
                    .background(
                        color,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                painter = painterResource(id = R.drawable.pedal_bike_24dp),
                contentDescription = "Location",
                tint = White,
            )

            Column(modifier = Modifier.width(140.dp)) {
                Text(
                    text = stationState.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "%.2f km · %s".format(stationState.distanceKm, stationState.minWalk),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stationState.freeBikes.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = color
                )

                Text(
                    text = stringResource(R.string.label_bikes),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stationState.emptySlots.toString(),
                    style = MaterialTheme.typography.titleLarge,
                )

                Text(
                    text = stringResource(R.string.label_docks),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.chevron_forward_24dp),
                contentDescription = "Go to details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

val sampleStations = listOf(
    StationsViewModel.StationState(
        id = "1",
        name = "Harbor Quay",
        freeBikes = 5,
        emptySlots = 10,
        latitude = 0.0,
        longitude = 0.0,
        address = "123 Main St",
        lastUpdated = "2024-06-01T12:00:00Z",
        distanceKm = 0.25,
        minWalk = "3 min walk",
        imageLink = "https://images.pexels.com/photos/37342833/pexels-photo-37342833.jpeg",
    ),
    StationsViewModel.StationState(
        id = "2",
        name = "Central Station",
        freeBikes = 0,
        emptySlots = 8,
        latitude = 0.0,
        longitude = 0.0,
        address = "456 Elm St",
        lastUpdated = "2024-06-01T12:05:00Z",
        distanceKm = 1.40,
        minWalk = "17 min walk",
        imageLink = "https://images.pexels.com/photos/37342833/pexels-photo-37342833.jpeg",
    ),
)

@Composable
@Preview
fun StationsScreenPreview() {
    StationsScreen(
        state = StationsUiState.Content(query = "", filter = StationFilter.ALL, stations = sampleStations),
        onClick = {},
        onSearchChange = {},
        onFilterChange = {},
        retry = {},
    )
}

@Composable
@Preview
fun StationsScreenEmptyPreview() {
    StationsScreen(
        state = StationsUiState.Content(query = "zzz", filter = StationFilter.ALL, stations = emptyList()),
        onClick = {},
        onSearchChange = {},
        onFilterChange = {},
        retry = {},
    )
}

package com.cardosogui.citybikesexplorer.selectBike

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun SelectBikeRoute(
    stationId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBikeSelected: (bikeId: String) -> Unit = {},
    viewModel: SelectBikeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(stationId) { viewModel.load(stationId) }
    SelectBikeScreen(
        modifier = modifier,
        state = state,
        onBackClick = onBackClick,
        onBikeSelected = onBikeSelected,
        retry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBikeScreen(
    modifier: Modifier = Modifier,
    state: SelectBikeUiState,
    onBackClick: () -> Unit,
    onBikeSelected: (bikeId: String) -> Unit,
    retry: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_a_bike)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                // The host Scaffold already consumes the status-bar inset, so don't add it again.
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
    ) { innerPadding ->
        when (state) {
            is SelectBikeUiState.Loading -> {
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

            is SelectBikeUiState.Error -> {
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

            is SelectBikeUiState.Success -> {
                SelectBikeContent(
                    modifier = Modifier.padding(innerPadding),
                    data = state.data,
                    onBikeSelected = onBikeSelected,
                )
            }
        }
    }
}

@Composable
private fun SelectBikeContent(
    data: SelectBikeViewModel.SelectBikeState,
    onBikeSelected: (bikeId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(White),
    ) {
        item { StationHeader(station = data.station) }
        item { HorizontalDivider() }
        item {
            Text(
                text = stringResource(R.string.available_bikes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
            )
        }
        items(data.bikes, key = { it.id }) { bike ->
            BikeRow(bike = bike, onSelect = { onBikeSelected(bike.id) })
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun StationHeader(
    station: StationsViewModel.StationState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(GreenActive, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.pedal_bike_24dp),
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.distance_format)
                    .format(station.distanceKm, station.minWalk),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun BikeRow(
    bike: StationsViewModel.BikeState,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.pedal_bike_24dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bike.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.battery_24dp),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.battery_format).format(bike.batteryPercent),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onSelect,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenActive),
        ) {
            Text(stringResource(R.string.select))
        }
    }
}

@Composable
@Preview
private fun SelectBikeScreenPreview() {
    val station = StationsViewModel.StationState(
        id = "st-001",
        name = "Central Station",
        freeBikes = 12,
        emptySlots = 8,
        latitude = 40.7128,
        longitude = -74.0060,
        address = "123 Main Street, Downtown",
        lastUpdated = "2026-06-13T08:45:00Z",
        distanceKm = 0.20,
        minWalk = "2 min walk",
        imageLink = "",
    )
    val bikes = listOf(
        StationsViewModel.BikeState("bk-4582", "Bike 4582", "st-001", batteryPercent = 82),
        StationsViewModel.BikeState("bk-4583", "Bike 4583", "st-001", batteryPercent = 76),
        StationsViewModel.BikeState("bk-4584", "Bike 4584", "st-001", batteryPercent = 68),
    )
    SelectBikeScreen(
        state = SelectBikeUiState.Success(SelectBikeViewModel.SelectBikeState(station, bikes)),
        onBackClick = {},
        onBikeSelected = {},
        retry = {},
    )
}

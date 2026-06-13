package com.cardosogui.citybikesexplorer.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.Orange
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun StationsScreen(
    modifier: Modifier = Modifier,
    state:  StationsUiState,
    onClick: () -> Unit,
    retry: () -> Unit
) {
    when (val state = state) {
        is StationsUiState.Loading -> {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is StationsUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                    ,
                    onClick = retry
                ) {
                    Text("Retry")
                }
            }
        }

        is StationsUiState.Success -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(White),
            ) {
                items(state.stations.stations, key = { it.id }) { station ->
                    StationCard(stationState = station, onClick = onClick)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun StationCard(
    modifier: Modifier = Modifier,
    stationState: StationsViewModel.StationState,
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
                    text = "0.2km * 2 min walk",
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
    ),
    StationsViewModel.StationState(
        id = "2",
        name = "Central Station",
        freeBikes = 2,
        emptySlots = 8,
        latitude = 0.0,
        longitude = 0.0,
        address = "456 Elm St",
        lastUpdated = "2024-06-01T12:05:00Z",
    ),
    StationsViewModel.StationState(
        id = "3",
        name = "Botanic Garden Gate",
        freeBikes = 0,
        emptySlots = 10,
        latitude = 0.0,
        longitude = 0.0,
        address = "456 Elm St",
        lastUpdated = "2024-06-01T12:05:00Z",
    ),
)

val stateSuccess = StationsUiState.Success(StationsViewModel.StationsState(sampleStations))


@Composable
@Preview
fun StationsScreenPreview() {
    StationsScreen(state = stateSuccess, onClick = {}, retry = {})
}

@Composable
@Preview
fun StationsScreenErrorPreview() {
    StationsScreen(state = StationsUiState.Error("An error occurred"), onClick = {}, retry = {})
}

package com.cardosogui.citybikesexplorer.stationDetail

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.White
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun StationDetailRoute(
    stationId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSelectBike: () -> Unit = {},
    viewModel: StationDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(stationId) { viewModel.load(stationId) }
    StationDetailScreen(
        modifier = modifier,
        state = state,
        onBackClick = onBackClick,
        onSelectBike = onSelectBike,
        retry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(
    modifier: Modifier = Modifier,
    state: StationDetailUiState,
    onBackClick: () -> Unit,
    onSelectBike: () -> Unit,
    retry: () -> Unit
) {
    val title = (state as? StationDetailUiState.Success)?.station?.name ?: stringResource(R.string.station_detail)
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            painter = painterResource(
                                if (isFavorite) R.drawable.favorite_24dp else R.drawable.favorite_border_24dp
                            ),
                            contentDescription = if (isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(
                                R.string.add_to_favorites
                            ),
                            tint = if (isFavorite) Color.Red else LocalContentColor.current,
                        )
                    }
                },
                // The host Scaffold already consumes the status-bar inset, so don't add it again.
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        bottomBar = {
            if (state is StationDetailUiState.Success) {
                Surface(color = White) {
                    Button(
                        onClick = onSelectBike,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenActive),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                    ) {
                        Text(text = stringResource(R.string.select_a_bike), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
    ) { innerPadding ->
        when (state) {
            is StationDetailUiState.Loading -> {
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

            is StationDetailUiState.Error -> {
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

            is StationDetailUiState.Success -> {
                StationDetailContent(
                    modifier = Modifier.padding(innerPadding),
                    station = state.station,
                )
            }
        }
    }
}

@Composable
private fun StationDetailContent(
    station: StationsViewModel.StationState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
    ) {
        StationImage(
            imageLink = station.imageLink,
            contentDescription = station.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                value = station.freeBikes.toString(),
                label = "Bikes available",
                valueColor = GreenActive,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = station.emptySlots.toString(),
                label = "Empty docks",
                valueColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }

        InfoRow(
            iconRes = R.drawable.place_24dp,
            label = stringResource(R.string.label_address),
            value = station.address,
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        InfoRow(
            iconRes = R.drawable.place_24dp,
            label = stringResource(R.string.label_distance),
            value = stringResource(R.string.distance_format).format(station.distanceKm, station.minWalk),
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        InfoRow(
            iconRes = R.drawable.my_location_24dp,
            label = stringResource(R.string.label_coordinates),
            value = stringResource(R.string.coordinates_format).format(station.latitude, station.longitude),
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        InfoRow(
            iconRes = R.drawable.schedule_24dp,
            label = stringResource(R.string.label_last_updated),
            value = formatRelativeTime(station.lastUpdated),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun InfoRow(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun StationImage(
    imageLink: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val placeholder = painterResource(R.drawable.placeholder_station_bikes)
    if (LocalInspectionMode.current) {
        // Compose previews can't fetch network images, so render the bundled placeholder.
        Image(
            painter = placeholder,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        AsyncImage(
            model = imageLink,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = modifier,
        )
    }
}

private fun formatRelativeTime(timestamp: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val millis = parser.parse(timestamp)?.time ?: return timestamp
        DateUtils.getRelativeTimeSpanString(
            millis,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
        ).toString()
    } catch (e: ParseException) {
        timestamp
    }
}

@Composable
@Preview
private fun StationDetailScreenPreview() {
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
    StationDetailScreen(
        state = StationDetailUiState.Success(station),
        onBackClick = {},
        onSelectBike = {},
        retry = {},
    )
}

package com.cardosogui.citybikesexplorer.rideSummary

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.ride.RidePricing
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun RideSummaryRoute(
    modifier: Modifier = Modifier,
    onBackToHome: () -> Unit = {},
    viewModel: RideSummaryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RideSummaryScreen(
        modifier = modifier,
        state = state,
        onBackToHome = onBackToHome,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideSummaryScreen(
    modifier: Modifier = Modifier,
    state: RideSummaryUiState,
    onBackToHome: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ride_summary)) },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24dp),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        bottomBar = {
            if (state is RideSummaryUiState.Success) {
                Surface(color = White) {
                    Button(
                        onClick = onBackToHome,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenActive),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                    ) {
                        Text(text = stringResource(R.string.back_to_home), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
    ) { innerPadding ->
        when (state) {
            is RideSummaryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }

            is RideSummaryUiState.Error -> {
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

            is RideSummaryUiState.Success -> {
                RideSummaryContent(
                    modifier = Modifier.padding(innerPadding),
                    summary = state.summary,
                )
            }
        }
    }
}

@Composable
private fun RideSummaryContent(
    summary: RideSummaryViewModel.SummaryUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(72.dp)
                    .background(GreenActive, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.check_24dp),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(40.dp),
                )
            }
            Text(
                text = stringResource(R.string.great_ride),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.thank_you_message),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        SummaryRow(
            iconRes = R.drawable.schedule_24dp,
            label = stringResource(R.string.label_time),
            value = formatDuration(summary.durationMillis),
        )
        HorizontalDivider()
        SummaryRow(
            iconRes = R.drawable.pedal_bike_24dp,
            label = stringResource(R.string.label_distance),
            value = stringResource(R.string.distance_km_format).format(summary.distanceKm),
        )
        HorizontalDivider()
        SummaryRow(
            iconRes = R.drawable.schedule_24dp,
            label = stringResource(R.string.label_total_cost),
            value = stringResource(R.string.total_cost_format).format(summary.totalCostNzd),
            valueSubtitle = stringResource(R.string.cost_breakdown_format)
                .format(RidePricing.UNLOCK_FEE_NZD, RidePricing.PER_MINUTE_NZD),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.label_station),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.stationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.bikes_docks_format)
                        .format(summary.bikesCount, summary.docksCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
            Icon(
                painter = painterResource(R.drawable.chevron_forward_24dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueSubtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )
            if (valueSubtitle != null) {
                Text(
                    text = valueSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    return "%02d:%02d:%02d".format(totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60)
}

@Composable
@Preview
private fun RideSummaryScreenPreview() {
    RideSummaryScreen(
        state = RideSummaryUiState.Success(
            RideSummaryViewModel.SummaryUi(
                durationMillis = 18L * 60_000 + 47_000,
                distanceKm = 3.4,
                totalCostNzd = 3.81,
                stationName = "Riverside Park",
                bikesCount = 5,
                docksCount = 10,
            ),
        ),
        onBackToHome = {},
    )
}

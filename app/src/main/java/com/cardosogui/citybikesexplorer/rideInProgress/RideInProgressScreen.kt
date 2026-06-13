package com.cardosogui.citybikesexplorer.rideInProgress

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cardosogui.citybikesexplorer.R
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import com.cardosogui.citybikesexplorer.ui.theme.White

@Composable
fun RideInProgressRoute(
    modifier: Modifier = Modifier,
    onRideEnded: () -> Unit = {},
    viewModel: RideInProgressViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val rideEnded by viewModel.rideEnded.collectAsStateWithLifecycle()
    val isRideInProgress by viewModel.isRideInProgress.collectAsStateWithLifecycle()

    // Block back navigation while a ride is active.
    BackHandler(enabled = isRideInProgress) { /* consumed: must end the ride first */ }

    LaunchedEffect(rideEnded) { if (rideEnded) onRideEnded() }

    RideInProgressScreen(
        modifier = modifier,
        uiState = uiState,
        onEndRide = viewModel::endRide,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideInProgressScreen(
    uiState: RideInProgressViewModel.RideUi,
    modifier: Modifier = Modifier,
    onEndRide: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ride_in_progress)) },
                navigationIcon = {
                    // Closing is intentionally a no-op: the ride must be ended first.
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.close_24dp),
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { }) {
                        Text(text = stringResource(R.string.help), color = GreenActive)
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        bottomBar = {
            Surface(color = White) {
                OutlinedButton(
                    onClick = onEndRide,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.end_ride), style = MaterialTheme.typography.titleMedium)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White),
        ) {
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.label_time),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                    Text(
                        text = uiState.time,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Metric(label = stringResource(R.string.label_distance), value = uiState.distance)
                        Metric(label = stringResource(R.string.label_calories), value = uiState.calories)
                    }
                }
            }

            // Map placeholder (no maps SDK integrated for this demo).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFE8EEF1), RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Composable
@Preview
private fun RideInProgressScreenPreview() {
    RideInProgressScreen(
        uiState = RideInProgressViewModel.RideUi(time = "00:07:32", distance = "1.6 km", calories = "48 kcal"),
    )
}

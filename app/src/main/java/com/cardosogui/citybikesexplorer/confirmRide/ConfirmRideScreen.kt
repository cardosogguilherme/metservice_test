package com.cardosogui.citybikesexplorer.confirmRide

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun ConfirmRideRoute(
    stationId: String,
    bikeId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onUnlocked: () -> Unit = {},
    viewModel: ConfirmRideViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val rideStarted by viewModel.rideStarted.collectAsStateWithLifecycle()

    LaunchedEffect(stationId, bikeId) { viewModel.load(stationId, bikeId) }
    LaunchedEffect(rideStarted) { if (rideStarted) onUnlocked() }

    ConfirmRideScreen(
        modifier = modifier,
        state = state,
        onBackClick = onBackClick,
        onUnlock = viewModel::unlock,
        retry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRideScreen(
    modifier: Modifier = Modifier,
    state: ConfirmRideUiState,
    onBackClick: () -> Unit,
    onUnlock: () -> Unit,
    retry: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.confirm_ride)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            if (state is ConfirmRideUiState.Success) {
                Surface(color = White) {
                    Button(
                        onClick = onUnlock,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenActive),
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
                        Text(
                            text = stringResource(R.string.unlock_bike),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        when (state) {
            is ConfirmRideUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(White),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }

            is ConfirmRideUiState.Error -> {
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

            is ConfirmRideUiState.Success -> {
                ConfirmRideContent(
                    modifier = Modifier.padding(innerPadding),
                    bike = state.bike,
                )
            }
        }
    }
}

@Composable
private fun ConfirmRideContent(
    bike: ConfirmRideViewModel.BikeUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.pedal_bike_24dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 24.dp)
                .size(140.dp),
        )

        Text(
            text = bike.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp),
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
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedCard(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.payment_method),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                    Text(
                        text = stringResource(R.string.payment_card),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.chevron_forward_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.schedule_24dp),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.charge_explanation),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.charge_detail),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
@Preview
private fun ConfirmRideScreenPreview() {
    ConfirmRideScreen(
        state = ConfirmRideUiState.Success(
            ConfirmRideViewModel.BikeUi(name = "Bike 4582", batteryPercent = 82),
        ),
        onBackClick = {},
        onUnlock = {},
        retry = {},
    )
}

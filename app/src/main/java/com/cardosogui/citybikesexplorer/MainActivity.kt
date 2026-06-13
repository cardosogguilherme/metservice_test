package com.cardosogui.citybikesexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cardosogui.citybikesexplorer.stations.StationsScreen
import com.cardosogui.citybikesexplorer.stations.StationsUiState
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import com.cardosogui.citybikesexplorer.ui.theme.CityBikesExplorerTheme
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import dagger.hilt.android.AndroidEntryPoint

                        @AndroidEntryPoint
                        class MainActivity : ComponentActivity() {
                            private val viewModel: StationsViewModel by viewModels<StationsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityBikesExplorerTheme {
                CityBikesExplorerApp()
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CityBikesExplorerApp() {
        var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

        val colors = NavigationSuiteItemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            navigationRailItemColors = NavigationRailItemColors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray,
                selectedTextColor = GreenActive,
                selectedIndicatorColor = Color.Transparent,
                unselectedTextColor = Color.Gray,
                disabledIconColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray
            )
        )

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                painterResource(it.icon),
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it },
                        colors = colors
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        val state = viewModel.uiState.collectAsState().value
                        StationsScreen(
                            modifier = Modifier.padding(innerPadding),
                            state = state,
                            onClick = { /* TODO: Handle station click */ },
                            retry = viewModel::retry
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Coming Soon: ${currentDestination.label}")
                        }}
                }
            }
        }
    }

    enum class AppDestinations(
        val label: String,
        val icon: Int,
    ) {
        HOME("Explore", R.drawable.ic_home),
        FAVORITES("Favorites", R.drawable.ic_favorite),
        PROFILE("Profile", R.drawable.ic_account_box),
    }
                            
    val sampleStations = listOf(
        StationsViewModel.StationState(
            id = "1",
            name = "Station 1",
            freeBikes = 5,
            emptySlots = 10,
            latitude = 0.0,
            longitude = 0.0,
            address = "123 Main St",
            lastUpdated = "2024-06-01T12:00:00Z",
        ),
        StationsViewModel.StationState(
            id = "2",
            name = "Station 2",
            freeBikes = 2,
            emptySlots = 8,
            latitude = 0.0,
            longitude = 0.0,
            address = "456 Elm St",
            lastUpdated = "2024-06-01T12:05:00Z",
        ),
        StationsViewModel.StationState(
            id = "3",
            name = "Station 3",
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
    fun CityBikesExplorerAppPreview() {
        CityBikesExplorerTheme {
            StationsScreen(
                modifier = Modifier,
                state = stateSuccess,
                onClick = { },
                retry = {  }
            )
        }
    }
}

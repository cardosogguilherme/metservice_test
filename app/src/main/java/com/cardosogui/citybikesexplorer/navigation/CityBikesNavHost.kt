package com.cardosogui.citybikesexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cardosogui.citybikesexplorer.stationDetail.StationDetailRoute
import com.cardosogui.citybikesexplorer.stations.StationsScreen
import com.cardosogui.citybikesexplorer.stations.StationsViewModel

object Routes {
    const val STATIONS = "stations"
    const val STATION_ID_ARG = "stationId"
    const val STATION_DETAIL = "stationDetail/{$STATION_ID_ARG}"

    fun stationDetail(stationId: String) = "stationDetail/$stationId"
}

@Composable
fun CityBikesNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.STATIONS,
        modifier = modifier,
    ) {
        composable(Routes.STATIONS) {
            val viewModel: StationsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            StationsScreen(
                state = state,
                onClick = { stationId -> navController.navigate(Routes.stationDetail(stationId)) },
                retry = viewModel::retry,
            )
        }

        composable(
            route = Routes.STATION_DETAIL,
            arguments = listOf(navArgument(Routes.STATION_ID_ARG) { type = NavType.StringType }),
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(Routes.STATION_ID_ARG).orEmpty()
            StationDetailRoute(
                stationId = stationId,
                onBackClick = { navController.popBackStack() },
                onSelectBike = { /* TODO: navigate to bike selection once that screen exists */ },
            )
        }
    }
}

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
import com.cardosogui.citybikesexplorer.confirmRide.ConfirmRideRoute
import com.cardosogui.citybikesexplorer.favorites.FavoritesRoute
import com.cardosogui.citybikesexplorer.profile.ProfileRoute
import com.cardosogui.citybikesexplorer.rideInProgress.RideInProgressRoute
import com.cardosogui.citybikesexplorer.rideSummary.RideSummaryRoute
import com.cardosogui.citybikesexplorer.selectBike.SelectBikeRoute
import com.cardosogui.citybikesexplorer.stationDetail.StationDetailRoute
import com.cardosogui.citybikesexplorer.stations.StationsScreen
import com.cardosogui.citybikesexplorer.stations.StationsViewModel

object Routes {
    const val STATIONS = "stations"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val STATION_ID_ARG = "stationId"
    const val BIKE_ID_ARG = "bikeId"
    const val STATION_DETAIL = "stationDetail/{$STATION_ID_ARG}"
    const val SELECT_BIKE = "selectBike/{$STATION_ID_ARG}"
    const val CONFIRM_RIDE = "confirmRide/{$STATION_ID_ARG}/{$BIKE_ID_ARG}"
    const val RIDE_IN_PROGRESS = "rideInProgress"
    const val RIDE_SUMMARY = "rideSummary"

    /** Destinations that show the bottom navigation. */
    val topLevelRoutes = setOf(STATIONS, FAVORITES, PROFILE)

    fun stationDetail(stationId: String) = "stationDetail/$stationId"
    fun selectBike(stationId: String) = "selectBike/$stationId"
    fun confirmRide(stationId: String, bikeId: String) = "confirmRide/$stationId/$bikeId"
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
                onSearchChange = viewModel::onSearchChange,
                onFilterChange = viewModel::onFilterChange,
                retry = viewModel::retry,
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesRoute(
                onStationClick = { stationId -> navController.navigate(Routes.stationDetail(stationId)) },
            )
        }

        composable(Routes.PROFILE) {
            ProfileRoute()
        }

        composable(
            route = Routes.STATION_DETAIL,
            arguments = listOf(navArgument(Routes.STATION_ID_ARG) { type = NavType.StringType }),
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(Routes.STATION_ID_ARG).orEmpty()
            StationDetailRoute(
                stationId = stationId,
                onBackClick = { navController.popBackStack() },
                onSelectBike = { navController.navigate(Routes.selectBike(stationId)) },
            )
        }

        composable(
            route = Routes.SELECT_BIKE,
            arguments = listOf(navArgument(Routes.STATION_ID_ARG) { type = NavType.StringType }),
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(Routes.STATION_ID_ARG).orEmpty()
            SelectBikeRoute(
                stationId = stationId,
                onBackClick = { navController.popBackStack() },
                onBikeSelected = { bikeId ->
                    navController.navigate(Routes.confirmRide(stationId, bikeId))
                },
            )
        }

        composable(
            route = Routes.CONFIRM_RIDE,
            arguments = listOf(
                navArgument(Routes.STATION_ID_ARG) { type = NavType.StringType },
                navArgument(Routes.BIKE_ID_ARG) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString(Routes.STATION_ID_ARG).orEmpty()
            val bikeId = backStackEntry.arguments?.getString(Routes.BIKE_ID_ARG).orEmpty()
            ConfirmRideRoute(
                stationId = stationId,
                bikeId = bikeId,
                onBackClick = { navController.popBackStack() },
                onUnlocked = { navController.navigate(Routes.RIDE_IN_PROGRESS) },
            )
        }

        composable(Routes.RIDE_IN_PROGRESS) {
            RideInProgressRoute(
                onRideEnded = {
                    // Ride finished: drop the ride flow from the back stack, leaving the summary above Stations.
                    navController.navigate(Routes.RIDE_SUMMARY) {
                        popUpTo(Routes.STATIONS) { inclusive = false }
                    }
                },
            )
        }

        composable(Routes.RIDE_SUMMARY) {
            RideSummaryRoute(
                onBackToHome = { navController.popBackStack(Routes.STATIONS, inclusive = false) },
            )
        }
    }
}

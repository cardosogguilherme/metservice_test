package com.cardosogui.citybikesexplorer.favorites

import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.toViewItem
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FavoritesInteractor @Inject constructor(
    private val stationRepository: StationRepository,
    favoritesRepository: FavoritesRepository,
) {
    val favoriteIds: StateFlow<Set<String>> = favoritesRepository.favoriteIds

    suspend fun getStations(): List<StationViewItem> =
        stationRepository.getStations().stationResponses.map { it.toViewItem() }
}

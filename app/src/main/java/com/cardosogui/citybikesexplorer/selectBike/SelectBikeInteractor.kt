package com.cardosogui.citybikesexplorer.selectBike

import com.cardosogui.citybikesexplorer.stations.BikeViewItem
import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.toViewItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SelectBikeInteractor @Inject constructor(
    private val repository: StationRepository,
) {
    suspend fun getSelectableBikes(stationId: String): SelectBikeViewItem = coroutineScope {
        // The header needs the station, the list needs its bikes — fetch both in parallel.
        val stationsDeferred = async { repository.getStations() }
        val bikesDeferred = async { repository.getBikes(stationId) }

        val station = stationsDeferred.await().stationResponses
            .firstOrNull { it.id == stationId }
            ?.toViewItem()
            ?: throw NoSuchElementException("No station found with id $stationId")
        val bikes = bikesDeferred.await().bikes.map { it.toViewItem() }

        SelectBikeViewItem(station = station, bikes = bikes)
    }
}

// region Data Models
data class SelectBikeViewItem(
    val station: StationViewItem,
    val bikes: List<BikeViewItem>,
)
// endregion

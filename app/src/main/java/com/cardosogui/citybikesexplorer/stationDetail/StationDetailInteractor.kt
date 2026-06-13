package com.cardosogui.citybikesexplorer.stationDetail

import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.toViewItem
import javax.inject.Inject

class StationDetailInteractor @Inject constructor(
    private val repository: StationRepository,
) {
    suspend fun getStationDetail(stationId: String): StationViewItem =
        repository.getStations().stationResponses
            .firstOrNull { it.id == stationId }
            ?.toViewItem()
            ?: throw NoSuchElementException("No station found with id $stationId")
}

package com.cardosogui.citybikesexplorer.confirmRide

import com.cardosogui.citybikesexplorer.stations.BikeViewItem
import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.toViewItem
import javax.inject.Inject

class ConfirmRideInteractor @Inject constructor(
    private val stationRepository: StationRepository,
) {
    suspend fun getBike(stationId: String, bikeId: String): BikeViewItem =
        stationRepository.getBikes(stationId).bikes
            .firstOrNull { it.id == bikeId }
            ?.toViewItem()
            ?: throw NoSuchElementException("No bike found with id $bikeId")
}

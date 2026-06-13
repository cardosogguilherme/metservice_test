package com.cardosogui.citybikesexplorer.rideSummary

import com.cardosogui.citybikesexplorer.ride.RidePricing
import com.cardosogui.citybikesexplorer.ride.RideRepository
import com.cardosogui.citybikesexplorer.stations.StationRepository
import javax.inject.Inject

class RideSummaryInteractor @Inject constructor(
    private val rideRepository: RideRepository,
    private val stationRepository: StationRepository,
) {
    suspend fun getSummary(): RideSummaryViewItem {
        val ride = rideRepository.lastFinishedRide ?: error("No finished ride available")
        val durationMillis = (ride.endedAtMillis ?: System.currentTimeMillis()) - ride.startedAtMillis
        val minutes = durationMillis / 60_000.0
        val distanceKm = minutes / 60.0 * AVG_SPEED_KMH
        val totalCost = RidePricing.UNLOCK_FEE_NZD + RidePricing.PER_MINUTE_NZD * minutes

        val station = stationRepository.getStations().stationResponses
            .firstOrNull { it.id == ride.stationId }

        return RideSummaryViewItem(
            durationMillis = durationMillis,
            distanceKm = distanceKm,
            totalCostNzd = totalCost,
            stationName = station?.name.orEmpty(),
            bikesCount = station?.freeBikes ?: 0,
            docksCount = station?.emptySlots ?: 0,
        )
    }

    private companion object {
        const val AVG_SPEED_KMH = 13.0
    }
}

// region Data Models
data class RideSummaryViewItem(
    val durationMillis: Long,
    val distanceKm: Double,
    val totalCostNzd: Double,
    val stationName: String,
    val bikesCount: Int,
    val docksCount: Int,
)
// endregion

package com.cardosogui.citybikesexplorer.stations

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.BikesResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Singleton

@Singleton
class StationsInteractor(
    private val repository: StationRepository,
) {
    suspend fun getStations() = repository.getStations().toViewItem()

    suspend fun getBikes(stationId: String) = repository.getBikes(stationId).toViewItem()
}

// region Data Models
@Serializable
data class StationsViewItem(
    val stationResponses: List<StationViewItem>,
)

@Serializable
data class StationViewItem(
    val id: String,
    val name: String,
    val freeBikes: Int,
    val emptySlots: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val lastUpdated: String,
)

@Serializable
data class BikesViewItem(
    val bikes: List<BikeViewItem>,
)

@Serializable
data class BikeViewItem(
    val id: String,
    val name: String,
    val stationId: String?,
)
// endregion

// region Mappers
private fun StationResponse.toViewItem() = StationViewItem(
    id = id,
    name = name,
    freeBikes = freeBikes,
    emptySlots = emptySlots,
    latitude = latitude,
    longitude = longitude,
    address = address,
    lastUpdated = lastUpdated,
)

private fun StationsResponse.toViewItem() = StationsViewItem(
    stationResponses = stationResponses.map { it.toViewItem() },
)

private fun BikeResponse.toViewItem() = BikeViewItem(
    id = id,
    name = name,
    stationId = stationId,
)

private fun BikesResponse.toViewItem() = BikesViewItem(
    bikes = bikes.map { it.toViewItem() },
)
// endregion
package com.cardosogui.citybikesexplorer.stations

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.BikesResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.math.roundToInt


class StationsInteractor @Inject constructor(
    private val repository: StationRepository,
) {
    suspend fun getStations() = repository.getStations().toViewItem()

    suspend fun getBikes(stationId: String) = repository.getBikes(stationId).toViewItem()
}

// region Data Models
@Serializable
data class StationsViewItem(
    val stations: List<StationViewItem>,
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
    val distanceKm: Double,
    val minWalk: String,
    val imageLink: String,
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
// Average walking pace ~5 km/h => 12 minutes per kilometre.
private const val WALK_MINUTES_PER_KM = 12

private fun walkTimeFor(distanceKm: Double): String {
    val minutes = (distanceKm * WALK_MINUTES_PER_KM).roundToInt().coerceAtLeast(1)
    return "$minutes min walk"
}

internal fun StationResponse.toViewItem() = StationViewItem(
    id = id,
    name = name,
    freeBikes = freeBikes,
    emptySlots = emptySlots,
    latitude = latitude,
    longitude = longitude,
    address = address,
    lastUpdated = lastUpdated,
    distanceKm = distanceKm,
    minWalk = walkTimeFor(distanceKm),
    imageLink = imageLink,
)

internal fun StationsResponse.toViewItem() = StationsViewItem(
    stations = stationResponses.map { it.toViewItem() },
)

internal fun BikeResponse.toViewItem() = BikeViewItem(
    id = id,
    name = name,
    stationId = stationId,
)

internal fun BikesResponse.toViewItem() = BikesViewItem(
    bikes = bikes.map { it.toViewItem() },
)
// endregion
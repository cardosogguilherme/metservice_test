package com.cardosogui.citybikesexplorer.testutil

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.BikesResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import com.cardosogui.citybikesexplorer.favorites.FavoritesRepository
import com.cardosogui.citybikesexplorer.ride.Ride
import com.cardosogui.citybikesexplorer.ride.RideRepository
import com.cardosogui.citybikesexplorer.stations.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

class FakeStationRepository(
    var stations: List<StationResponse> = emptyList(),
    var bikesByStation: Map<String, List<BikeResponse>> = emptyMap(),
    var failStations: Boolean = false,
    var failBikes: Boolean = false,
) : StationRepository {

    override suspend fun getStations(): StationsResponse {
        if (failStations) throw IOException("stations failed")
        return StationsResponse(stations)
    }

    override suspend fun getBikes(stationId: String): BikesResponse {
        if (failBikes) throw IOException("bikes failed")
        return BikesResponse(bikesByStation[stationId].orEmpty())
    }
}

class FakeFavoritesRepository(initial: Set<String> = emptySet()) : FavoritesRepository {
    private val _favoriteIds = MutableStateFlow(initial)
    override val favoriteIds: StateFlow<Set<String>> = _favoriteIds

    override fun toggle(stationId: String) {
        _favoriteIds.update { if (stationId in it) it - stationId else it + stationId }
    }
}

class FakeRideRepository(
    var failInitiate: Boolean = false,
    var failFinish: Boolean = false,
) : RideRepository {

    private val _isRideInProgress = MutableStateFlow(false)
    override val isRideInProgress: StateFlow<Boolean> = _isRideInProgress

    override var currentRide: Ride? = null
        private set
    override var lastFinishedRide: Ride? = null
        private set

    var initiateCount = 0
        private set
    var finishCount = 0
        private set

    override suspend fun initiateRide(bikeId: String, stationId: String) {
        if (failInitiate) throw IOException("initiate failed")
        initiateCount++
        currentRide = Ride(bikeId = bikeId, stationId = stationId, startedAtMillis = 0L)
        _isRideInProgress.value = true
    }

    override suspend fun finishRide(): Ride {
        if (failFinish) throw IOException("finish failed")
        finishCount++
        val finished = (currentRide ?: error("no ride in progress")).copy(endedAtMillis = 60_000L)
        lastFinishedRide = finished
        currentRide = null
        _isRideInProgress.value = false
        return finished
    }
}

fun stationResponse(
    id: String,
    name: String = "Station $id",
    freeBikes: Int = 5,
    emptySlots: Int = 5,
    distanceKm: Double = 0.5,
): StationResponse = StationResponse(
    id = id,
    name = name,
    freeBikes = freeBikes,
    emptySlots = emptySlots,
    latitude = 0.0,
    longitude = 0.0,
    address = "$name address",
    lastUpdated = "2026-06-13T08:00:00Z",
    distanceKm = distanceKm,
    imageLink = "https://example.com/$id.jpg",
)

fun bikeResponse(
    id: String,
    stationId: String,
    name: String = "Bike $id",
    batteryPercent: Int = 80,
): BikeResponse = BikeResponse(
    id = id,
    name = name,
    stationId = stationId,
    batteryPercent = batteryPercent,
)

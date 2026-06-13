package com.cardosogui.citybikesexplorer.ride

import com.cardosogui.citybikesexplorer.data.remote.RideApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class Ride(
    val bikeId: String,
    val stationId: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long? = null,
)

/** Pricing is fixed for this demo (NZD). */
object RidePricing {
    const val UNLOCK_FEE_NZD = 1.00
    const val PER_MINUTE_NZD = 0.20
}

interface RideRepository {
    /** True from initiateRide until finishRide; the UI uses this to block back navigation. */
    val isRideInProgress: StateFlow<Boolean>
    val currentRide: Ride?
    val lastFinishedRide: Ride?

    suspend fun initiateRide(bikeId: String, stationId: String)
    suspend fun finishRide(): Ride
}

@Singleton
class RideRepositoryImpl @Inject constructor(
    private val api: RideApi,
    private val dispatcher: CoroutineDispatcher,
) : RideRepository {

    private val _isRideInProgress = MutableStateFlow(false)
    override val isRideInProgress: StateFlow<Boolean> = _isRideInProgress.asStateFlow()

    override var currentRide: Ride? = null
        private set
    override var lastFinishedRide: Ride? = null
        private set

    override suspend fun initiateRide(bikeId: String, stationId: String) {
        // Dummy backend call; the mock asset just returns {"status":"OK"}.
        withContext(dispatcher) { api.initiateRide() }
        currentRide = Ride(
            bikeId = bikeId,
            stationId = stationId,
            startedAtMillis = System.currentTimeMillis(),
        )
        _isRideInProgress.value = true
    }

    override suspend fun finishRide(): Ride {
        withContext(dispatcher) { api.finishRide() }
        val finished = (currentRide ?: error("No ride in progress"))
            .copy(endedAtMillis = System.currentTimeMillis())
        lastFinishedRide = finished
        currentRide = null
        _isRideInProgress.value = false
        return finished
    }
}

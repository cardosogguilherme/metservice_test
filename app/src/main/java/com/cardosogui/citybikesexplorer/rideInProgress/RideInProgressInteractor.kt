package com.cardosogui.citybikesexplorer.rideInProgress

import com.cardosogui.citybikesexplorer.ride.Ride
import com.cardosogui.citybikesexplorer.ride.RideRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RideInProgressInteractor @Inject constructor(
    private val rideRepository: RideRepository,
) {
    val isRideInProgress: StateFlow<Boolean> = rideRepository.isRideInProgress

    fun startedAtMillis(): Long? = rideRepository.currentRide?.startedAtMillis

    suspend fun endRide(): Ride = rideRepository.finishRide()
}

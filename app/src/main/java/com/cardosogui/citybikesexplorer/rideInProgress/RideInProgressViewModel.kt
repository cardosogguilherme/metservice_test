package com.cardosogui.citybikesexplorer.rideInProgress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideInProgressViewModel @Inject constructor(
    private val interactor: RideInProgressInteractor,
) : ViewModel() {

    val isRideInProgress: StateFlow<Boolean> = interactor.isRideInProgress

    private val _uiState = MutableStateFlow(RideUi("00:00:00", "0.0 km", "0 kcal"))
    val uiState: StateFlow<RideUi> = _uiState.asStateFlow()

    private val _rideEnded = MutableStateFlow(false)
    val rideEnded: StateFlow<Boolean> = _rideEnded.asStateFlow()

    init {
        viewModelScope.launch {
            // Tick once a second while the ride is active; stop as soon as it ends.
            var startedAt = interactor.startedAtMillis()
            while (isActive && startedAt != null) {
                _uiState.value = buildUi(System.currentTimeMillis() - startedAt)
                delay(1000)
                startedAt = interactor.startedAtMillis()
            }
        }
    }

    fun endRide() {
        viewModelScope.launch {
            runCatching { interactor.endRide() }
            _rideEnded.value = true
        }
    }

    private fun buildUi(elapsedMillis: Long): RideUi {
        val totalSeconds = elapsedMillis / 1000
        val time = "%02d:%02d:%02d".format(totalSeconds / 3600, (totalSeconds % 3600) / 60, totalSeconds % 60)
        val minutes = elapsedMillis / 60_000.0
        val km = minutes / 60.0 * AVG_SPEED_KMH
        val calories = (minutes * CALORIES_PER_MINUTE).toInt()
        return RideUi(
            time = time,
            distance = "%.1f km".format(km),
            calories = "$calories kcal",
        )
    }

    data class RideUi(
        val time: String,
        val distance: String,
        val calories: String,
    )

    private companion object {
        const val AVG_SPEED_KMH = 13.0
        const val CALORIES_PER_MINUTE = 6.4
    }
}

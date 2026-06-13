package com.cardosogui.citybikesexplorer.confirmRide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardosogui.citybikesexplorer.ride.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ConfirmRideUiState {
    data object Loading : ConfirmRideUiState
    data class Success(val bike: ConfirmRideViewModel.BikeUi) : ConfirmRideUiState
    data class Error(val message: String) : ConfirmRideUiState
}

@HiltViewModel
class ConfirmRideViewModel @Inject constructor(
    private val interactor: ConfirmRideInteractor,
    private val rideRepository: RideRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConfirmRideUiState>(ConfirmRideUiState.Loading)
    val uiState: StateFlow<ConfirmRideUiState> = _uiState.asStateFlow()

    private val _rideStarted = MutableStateFlow(false)
    val rideStarted: StateFlow<Boolean> = _rideStarted.asStateFlow()

    private var stationId: String = ""
    private var bikeId: String = ""

    fun load(stationId: String, bikeId: String) {
        this.stationId = stationId
        this.bikeId = bikeId
        _uiState.value = ConfirmRideUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val bike = interactor.getBike(stationId, bikeId)
                ConfirmRideUiState.Success(BikeUi(name = bike.name, batteryPercent = bike.batteryPercent))
            } catch (e: Exception) {
                ConfirmRideUiState.Error(e.message ?: "Failed to load bike")
            }
        }
    }

    fun retry() = load(stationId, bikeId)

    fun unlock() {
        viewModelScope.launch {
            try {
                rideRepository.initiateRide(bikeId = bikeId, stationId = stationId)
                _rideStarted.value = true
            } catch (e: Exception) {
                _uiState.value = ConfirmRideUiState.Error(e.message ?: "Failed to unlock bike")
            }
        }
    }

    data class BikeUi(
        val name: String,
        val batteryPercent: Int,
    )
}

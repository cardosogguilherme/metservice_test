package com.cardosogui.citybikesexplorer.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

sealed interface StationsUiState {
    data object Loading : StationsUiState
    data class Success(val stations: StationsViewModel.StationsState) : StationsUiState
    data class Error(val message: String) : StationsUiState
}

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val interactor: StationsInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StationsUiState>(StationsUiState.Loading)
    val uiState: StateFlow<StationsUiState> = _uiState.asStateFlow()

    init {
        loadStations()
    }

    fun retry() = loadStations()

    private fun loadStations() {
        _uiState.value = StationsUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                StationsUiState.Success(interactor.getStations().toState())
            } catch (e: Exception) {
                StationsUiState.Error(e.message ?: "Failed to load stations")
            }
        }
    }

    // region Data Models
    @Serializable
    data class StationsState(
        val stations: List<StationState>,
    )

    @Serializable
    data class StationState(
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
    data class BikesState(
        val bikes: List<BikeState>,
    )

    @Serializable
    data class BikeState(
        val id: String,
        val name: String,
        val stationId: String?,
    )
// endregion

    // region Mappers
    private fun StationViewItem.toState() = StationState(
        id = id,
        name = name,
        freeBikes = freeBikes,
        emptySlots = emptySlots,
        latitude = latitude,
        longitude = longitude,
        address = address,
        lastUpdated = lastUpdated,
    )

    private fun StationsViewItem.toState() = StationsState(
        stations = stations.map { it.toState() },
    )

    private fun BikeViewItem.toState() = BikeState(
        id = id,
        name = name,
        stationId = stationId,
    )

    private fun BikesViewItem.toState() = BikesState(
        bikes = bikes.map { it.toState() },
    )
// endregion
}
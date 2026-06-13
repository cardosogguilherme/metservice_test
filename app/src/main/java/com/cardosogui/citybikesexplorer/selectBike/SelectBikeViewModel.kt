package com.cardosogui.citybikesexplorer.selectBike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardosogui.citybikesexplorer.stations.BikeViewItem
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SelectBikeUiState {
    data object Loading : SelectBikeUiState
    data class Success(val data: SelectBikeViewModel.SelectBikeState) : SelectBikeUiState
    data class Error(val message: String) : SelectBikeUiState
}

@HiltViewModel
class SelectBikeViewModel @Inject constructor(
    private val interactor: SelectBikeInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SelectBikeUiState>(SelectBikeUiState.Loading)
    val uiState: StateFlow<SelectBikeUiState> = _uiState.asStateFlow()

    private var stationId: String? = null

    fun load(stationId: String) {
        this.stationId = stationId
        _uiState.value = SelectBikeUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                SelectBikeUiState.Success(interactor.getSelectableBikes(stationId).toState())
            } catch (e: Exception) {
                SelectBikeUiState.Error(e.message ?: "Failed to load bikes")
            }
        }
    }

    fun retry() {
        stationId?.let { load(it) }
    }

    // region Data Models
    data class SelectBikeState(
        val station: StationsViewModel.StationState,
        val bikes: List<StationsViewModel.BikeState>,
    )
    // endregion

    // region Mappers
    private fun SelectBikeViewItem.toState() = SelectBikeState(
        station = station.toState(),
        bikes = bikes.map { it.toState() },
    )

    private fun StationViewItem.toState() = StationsViewModel.StationState(
        id = id,
        name = name,
        freeBikes = freeBikes,
        emptySlots = emptySlots,
        latitude = latitude,
        longitude = longitude,
        address = address,
        lastUpdated = lastUpdated,
        distanceKm = distanceKm,
        minWalk = minWalk,
        imageLink = imageLink,
    )

    private fun BikeViewItem.toState() = StationsViewModel.BikeState(
        id = id,
        name = name,
        stationId = stationId,
        batteryPercent = batteryPercent,
    )
    // endregion
}

package com.cardosogui.citybikesexplorer.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

enum class StationFilter { ALL, AVAILABLE, UNAVAILABLE }

sealed interface StationsUiState {
    data object Loading : StationsUiState
    data class Error(val message: String) : StationsUiState

    /** Loaded list (already filtered by [query] and [filter]); [stations] may be empty. */
    data class Content(
        val query: String,
        val filter: StationFilter,
        val stations: List<StationsViewModel.StationState>,
    ) : StationsUiState
}

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val interactor: StationsInteractor,
) : ViewModel() {

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Loaded(val stations: List<StationState>) : LoadState
        data class Failed(val message: String) : LoadState
    }

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val _query = MutableStateFlow("")
    private val _filter = MutableStateFlow(StationFilter.ALL)

    val uiState: StateFlow<StationsUiState> =
        combine(_loadState, _query, _filter) { load, query, filter ->
            when (load) {
                is LoadState.Loading -> StationsUiState.Loading
                is LoadState.Failed -> StationsUiState.Error(load.message)
                is LoadState.Loaded -> StationsUiState.Content(
                    query = query,
                    filter = filter,
                    stations = load.stations.applyFilters(query, filter),
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StationsUiState.Loading)

    init {
        loadStations()
    }

    fun retry() = loadStations()

    fun onSearchChange(query: String) {
        _query.value = query
    }

    fun onFilterChange(filter: StationFilter) {
        _filter.value = filter
    }

    private fun loadStations() {
        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            _loadState.value = try {
                LoadState.Loaded(interactor.getStations().toState().stations)
            } catch (e: Exception) {
                LoadState.Failed(e.message ?: "Failed to load stations")
            }
        }
    }

    private fun List<StationState>.applyFilters(query: String, filter: StationFilter): List<StationState> {
        val byAvailability = when (filter) {
            StationFilter.ALL -> this
            StationFilter.AVAILABLE -> filter { it.freeBikes > 0 }
            StationFilter.UNAVAILABLE -> filter { it.freeBikes == 0 }
        }
        // Only start searching once the query is at least MIN_SEARCH_LENGTH characters.
        return if (query.length >= MIN_SEARCH_LENGTH) {
            byAvailability.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            byAvailability
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
        val distanceKm: Double,
        val minWalk: String,
        val imageLink: String,
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
        val batteryPercent: Int,
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
        distanceKm = distanceKm,
        minWalk = minWalk,
        imageLink = imageLink,
    )

    private fun StationsViewItem.toState() = StationsState(
        stations = stations.map { it.toState() },
    )

    private fun BikeViewItem.toState() = BikeState(
        id = id,
        name = name,
        stationId = stationId,
        batteryPercent = batteryPercent,
    )

    private fun BikesViewItem.toState() = BikesState(
        bikes = bikes.map { it.toState() },
    )
// endregion

    private companion object {
        const val MIN_SEARCH_LENGTH = 3
    }
}

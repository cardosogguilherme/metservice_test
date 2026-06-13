package com.cardosogui.citybikesexplorer.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState

    /** Favorited stations; [stations] is empty when nothing has been favorited. */
    data class Content(val stations: List<StationsViewModel.StationState>) : FavoritesUiState
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val interactor: FavoritesInteractor,
) : ViewModel() {

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Loaded(val stations: List<StationsViewModel.StationState>) : LoadState
        data class Failed(val message: String) : LoadState
    }

    private val _loadState = MutableStateFlow<LoadState>(LoadState.Loading)

    val uiState: StateFlow<FavoritesUiState> =
        combine(_loadState, interactor.favoriteIds) { load, favoriteIds ->
            when (load) {
                is LoadState.Loading -> FavoritesUiState.Loading
                is LoadState.Failed -> FavoritesUiState.Error(load.message)
                is LoadState.Loaded -> FavoritesUiState.Content(
                    stations = load.stations.filter { it.id in favoriteIds },
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState.Loading)

    init {
        load()
    }

    fun load() {
        _loadState.value = LoadState.Loading
        viewModelScope.launch {
            _loadState.value = try {
                LoadState.Loaded(interactor.getStations().map { it.toState() })
            } catch (e: Exception) {
                LoadState.Failed(e.message ?: "Failed to load favorites")
            }
        }
    }

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
}

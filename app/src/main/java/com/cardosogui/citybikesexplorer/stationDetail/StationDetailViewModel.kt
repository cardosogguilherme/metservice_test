package com.cardosogui.citybikesexplorer.stationDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardosogui.citybikesexplorer.favorites.FavoritesRepository
import com.cardosogui.citybikesexplorer.stations.StationViewItem
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface StationDetailUiState {
    data object Loading : StationDetailUiState
    data class Success(val station: StationsViewModel.StationState) : StationDetailUiState
    data class Error(val message: String) : StationDetailUiState
}

@HiltViewModel
class StationDetailViewModel @Inject constructor(
    private val interactor: StationDetailInteractor,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StationDetailUiState>(StationDetailUiState.Loading)
    val uiState: StateFlow<StationDetailUiState> = _uiState.asStateFlow()

    private val _stationId = MutableStateFlow<String?>(null)

    val isFavorite: StateFlow<Boolean> =
        combine(_stationId, favoritesRepository.favoriteIds) { id, favorites ->
            id != null && id in favorites
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun load(stationId: String) {
        _stationId.value = stationId
        _uiState.value = StationDetailUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                StationDetailUiState.Success(interactor.getStationDetail(stationId).toState())
            } catch (e: Exception) {
                StationDetailUiState.Error(e.message ?: "Failed to load station detail")
            }
        }
    }

    fun retry() {
        _stationId.value?.let { load(it) }
    }

    fun toggleFavorite() {
        _stationId.value?.let { favoritesRepository.toggle(it) }
    }

    // region Mappers
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
    // endregion
}

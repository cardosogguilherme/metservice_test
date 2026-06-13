package com.cardosogui.citybikesexplorer.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StationsUiState {
    data object Loading : StationsUiState
    data class Success(val stationResponses: List<StationResponse>) : StationsUiState
    data class Error(val message: String) : StationsUiState
}

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val repository: StationRepository,
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
                StationsUiState.Success(repository.getStations())
            } catch (e: Exception) {
                StationsUiState.Error(e.message ?: "Failed to load stations")
            }
        }
    }
}

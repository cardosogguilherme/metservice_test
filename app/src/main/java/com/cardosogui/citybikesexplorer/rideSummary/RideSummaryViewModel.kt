package com.cardosogui.citybikesexplorer.rideSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RideSummaryUiState {
    data object Loading : RideSummaryUiState
    data class Success(val summary: RideSummaryViewModel.SummaryUi) : RideSummaryUiState
    data class Error(val message: String) : RideSummaryUiState
}

@HiltViewModel
class RideSummaryViewModel @Inject constructor(
    private val interactor: RideSummaryInteractor,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RideSummaryUiState>(RideSummaryUiState.Loading)
    val uiState: StateFlow<RideSummaryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = RideSummaryUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                RideSummaryUiState.Success(interactor.getSummary().toUi())
            } catch (e: Exception) {
                RideSummaryUiState.Error(e.message ?: "Failed to load ride summary")
            }
        }
    }

    // region Data Models
    data class SummaryUi(
        val durationMillis: Long,
        val distanceKm: Double,
        val totalCostNzd: Double,
        val stationName: String,
        val bikesCount: Int,
        val docksCount: Int,
    )
    // endregion

    // region Mappers
    private fun RideSummaryViewItem.toUi() = SummaryUi(
        durationMillis = durationMillis,
        distanceKm = distanceKm,
        totalCostNzd = totalCostNzd,
        stationName = stationName,
        bikesCount = bikesCount,
        docksCount = docksCount,
    )
    // endregion
}

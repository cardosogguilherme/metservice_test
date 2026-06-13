package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.StationsUiState
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts in Loading then emits Success with stations from repository`() = runTest {
        val stationResponses = listOf(
            StationResponse(
                "st-001",
                "Harbour Quay",
                freeBikes = 12,
                emptySlots = 4,
                latitude = -41.28,
                longitude = 174.77,
                address = "23 Customhouse Quay, Wellington 6011",
                lastUpdated = "2026-06-13T08:45:00Z",
            ),
        )
        val viewModel = StationsViewModel(FakeStationRepository(Result.success(stationResponses)))

        assertEquals(StationsUiState.Loading, viewModel.uiState.value)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StationsUiState.Success(stationResponses), viewModel.uiState.value)
    }

    @Test
    fun `emits Error when repository fails and recovers on retry`() = runTest {
        val repository = FakeStationRepository(Result.failure(IOException("boom")))
        val viewModel = StationsViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is StationsUiState.Error)

        repository.result = Result.success(emptyList())
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StationsUiState.Success(emptyList()), viewModel.uiState.value)
    }

    private class FakeStationRepository(var result: Result<List<StationResponse>>) : StationRepository {
        override suspend fun getStations(): List<StationResponse> = result.getOrThrow()
        override suspend fun getBikes(stationId: String): List<BikeResponse> = emptyList()
    }
}

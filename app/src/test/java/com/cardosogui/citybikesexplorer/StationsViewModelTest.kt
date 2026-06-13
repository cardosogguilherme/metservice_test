package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.data.model.Station
import com.cardosogui.citybikesexplorer.data.repository.StationRepository
import com.cardosogui.citybikesexplorer.ui.stations.StationsUiState
import com.cardosogui.citybikesexplorer.ui.stations.StationsViewModel
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
        val stations = listOf(
            Station("st-001", "Harbour Quay", freeBikes = 12, emptySlots = 4, latitude = -41.28, longitude = 174.77),
        )
        val viewModel = StationsViewModel(FakeStationRepository(Result.success(stations)))

        assertEquals(StationsUiState.Loading, viewModel.uiState.value)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StationsUiState.Success(stations), viewModel.uiState.value)
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

    private class FakeStationRepository(var result: Result<List<Station>>) : StationRepository {
        override suspend fun getStations(): List<Station> = result.getOrThrow()
    }
}

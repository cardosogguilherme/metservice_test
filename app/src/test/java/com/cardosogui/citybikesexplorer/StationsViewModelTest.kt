package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.stations.StationFilter
import com.cardosogui.citybikesexplorer.stations.StationsInteractor
import com.cardosogui.citybikesexplorer.stations.StationsUiState
import com.cardosogui.citybikesexplorer.stations.StationsViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeStationRepository
import com.cardosogui.citybikesexplorer.testutil.stationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repo: FakeStationRepository) =
        StationsViewModel(StationsInteractor(repo))

    private fun TestScope.collect(vm: StationsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
    }

    private fun content(vm: StationsViewModel) = vm.uiState.value as StationsUiState.Content

    @Test
    fun `loads all stations`() = runTest {
        val repo = FakeStationRepository(
            stations = listOf(
                stationResponse("1", "Harbour Quay", freeBikes = 3),
                stationResponse("2", "Central Station", freeBikes = 0),
            ),
        )
        val vm = viewModel(repo)
        collect(vm)
        advanceUntilIdle()

        assertEquals(2, content(vm).stations.size)
    }

    @Test
    fun `search starts filtering only at three characters`() = runTest {
        val repo = FakeStationRepository(
            stations = listOf(
                stationResponse("1", "Harbour Quay"),
                stationResponse("2", "Central Station"),
            ),
        )
        val vm = viewModel(repo)
        collect(vm)
        advanceUntilIdle()

        vm.onSearchChange("ce")
        advanceUntilIdle()
        assertEquals(2, content(vm).stations.size)

        vm.onSearchChange("cen")
        advanceUntilIdle()
        assertEquals(listOf("Central Station"), content(vm).stations.map { it.name })
    }

    @Test
    fun `search with no match yields empty content`() = runTest {
        val repo = FakeStationRepository(stations = listOf(stationResponse("1", "Harbour Quay")))
        val vm = viewModel(repo)
        collect(vm)
        advanceUntilIdle()

        vm.onSearchChange("zzz")
        advanceUntilIdle()
        assertTrue(content(vm).stations.isEmpty())
    }

    @Test
    fun `availability filter keeps only matching stations`() = runTest {
        val repo = FakeStationRepository(
            stations = listOf(
                stationResponse("1", "A", freeBikes = 4),
                stationResponse("2", "B", freeBikes = 0),
            ),
        )
        val vm = viewModel(repo)
        collect(vm)
        advanceUntilIdle()

        vm.onFilterChange(StationFilter.AVAILABLE)
        advanceUntilIdle()
        assertEquals(listOf("1"), content(vm).stations.map { it.id })

        vm.onFilterChange(StationFilter.UNAVAILABLE)
        advanceUntilIdle()
        assertEquals(listOf("2"), content(vm).stations.map { it.id })
    }

    @Test
    fun `load failure emits error`() = runTest {
        val vm = viewModel(FakeStationRepository(failStations = true))
        collect(vm)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is StationsUiState.Error)
    }
}

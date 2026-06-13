package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.rideSummary.RideSummaryInteractor
import com.cardosogui.citybikesexplorer.rideSummary.RideSummaryUiState
import com.cardosogui.citybikesexplorer.rideSummary.RideSummaryViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeRideRepository
import com.cardosogui.citybikesexplorer.testutil.FakeStationRepository
import com.cardosogui.citybikesexplorer.testutil.stationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class RideSummaryViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `summarises the finished ride and its station`() = runTest {
        val ride = FakeRideRepository()
        ride.initiateRide(bikeId = "b1", stationId = "st-1")
        ride.finishRide() // start 0ms, end 60_000ms -> 1 minute

        val stationRepo = FakeStationRepository(
            stations = listOf(stationResponse("st-1", "Riverside Park", freeBikes = 5, emptySlots = 10)),
        )
        val vm = RideSummaryViewModel(RideSummaryInteractor(ride, stationRepo))
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is RideSummaryUiState.Success)
        val summary = (state as RideSummaryUiState.Success).summary
        assertEquals("Riverside Park", summary.stationName)
        assertEquals(5, summary.bikesCount)
        assertEquals(10, summary.docksCount)
        assertEquals(60_000L, summary.durationMillis)
        // NZD $1.00 unlock + NZD $0.20 * 1 minute
        assertEquals(1.20, summary.totalCostNzd, 0.001)
    }

    @Test
    fun `no finished ride emits error`() = runTest {
        val stationRepo = FakeStationRepository(stations = listOf(stationResponse("st-1")))
        val vm = RideSummaryViewModel(RideSummaryInteractor(FakeRideRepository(), stationRepo))
        advanceUntilIdle()

        assertTrue(vm.uiState.value is RideSummaryUiState.Error)
    }
}

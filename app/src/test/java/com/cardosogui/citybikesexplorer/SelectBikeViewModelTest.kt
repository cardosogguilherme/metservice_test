package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.selectBike.SelectBikeInteractor
import com.cardosogui.citybikesexplorer.selectBike.SelectBikeUiState
import com.cardosogui.citybikesexplorer.selectBike.SelectBikeViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeStationRepository
import com.cardosogui.citybikesexplorer.testutil.bikeResponse
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
class SelectBikeViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `loads station header and its bikes`() = runTest {
        val repo = FakeStationRepository(
            stations = listOf(stationResponse("st-1", "Central Station")),
            bikesByStation = mapOf(
                "st-1" to listOf(
                    bikeResponse("b1", "st-1", batteryPercent = 82),
                    bikeResponse("b2", "st-1", batteryPercent = 70),
                ),
            ),
        )
        val vm = SelectBikeViewModel(SelectBikeInteractor(repo))

        vm.load("st-1")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is SelectBikeUiState.Success)
        val data = (state as SelectBikeUiState.Success).data
        assertEquals("Central Station", data.station.name)
        assertEquals(2, data.bikes.size)
        assertEquals(82, data.bikes.first().batteryPercent)
    }

    @Test
    fun `failure emits error`() = runTest {
        val repo = FakeStationRepository(
            stations = listOf(stationResponse("st-1")),
            failBikes = true,
        )
        val vm = SelectBikeViewModel(SelectBikeInteractor(repo))

        vm.load("st-1")
        advanceUntilIdle()

        assertTrue(vm.uiState.value is SelectBikeUiState.Error)
    }
}

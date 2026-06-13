package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.confirmRide.ConfirmRideInteractor
import com.cardosogui.citybikesexplorer.confirmRide.ConfirmRideUiState
import com.cardosogui.citybikesexplorer.confirmRide.ConfirmRideViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeRideRepository
import com.cardosogui.citybikesexplorer.testutil.FakeStationRepository
import com.cardosogui.citybikesexplorer.testutil.bikeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmRideViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repo: FakeStationRepository, ride: FakeRideRepository) =
        ConfirmRideViewModel(ConfirmRideInteractor(repo), ride)

    @Test
    fun `loads the chosen bike`() = runTest {
        val repo = FakeStationRepository(
            bikesByStation = mapOf("st-1" to listOf(bikeResponse("b1", "st-1", name = "Bike 4582", batteryPercent = 82))),
        )
        val vm = viewModel(repo, FakeRideRepository())

        vm.load("st-1", "b1")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is ConfirmRideUiState.Success)
        assertEquals("Bike 4582", (state as ConfirmRideUiState.Success).bike.name)
        assertEquals(82, state.bike.batteryPercent)
    }

    @Test
    fun `unlock initiates a ride and flags rideStarted`() = runTest {
        val repo = FakeStationRepository(
            bikesByStation = mapOf("st-1" to listOf(bikeResponse("b1", "st-1"))),
        )
        val ride = FakeRideRepository()
        val vm = viewModel(repo, ride)

        vm.load("st-1", "b1")
        advanceUntilIdle()
        assertFalse(vm.rideStarted.value)

        vm.unlock()
        advanceUntilIdle()

        assertTrue(vm.rideStarted.value)
        assertEquals(1, ride.initiateCount)
        assertTrue(ride.isRideInProgress.value)
    }
}

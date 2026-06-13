package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.rideInProgress.RideInProgressInteractor
import com.cardosogui.citybikesexplorer.rideInProgress.RideInProgressViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeRideRepository
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
class RideInProgressViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `endRide finishes the ride and flags rideEnded`() = runTest {
        val ride = FakeRideRepository()
        ride.initiateRide(bikeId = "b1", stationId = "st-1")
        val vm = RideInProgressViewModel(RideInProgressInteractor(ride))

        vm.endRide()
        advanceUntilIdle()

        assertTrue(vm.rideEnded.value)
        assertEquals(1, ride.finishCount)
        assertFalse(ride.isRideInProgress.value)
    }
}

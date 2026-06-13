package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.stationDetail.StationDetailInteractor
import com.cardosogui.citybikesexplorer.stationDetail.StationDetailUiState
import com.cardosogui.citybikesexplorer.stationDetail.StationDetailViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeFavoritesRepository
import com.cardosogui.citybikesexplorer.testutil.FakeStationRepository
import com.cardosogui.citybikesexplorer.testutil.stationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class StationDetailViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `loads the requested station`() = runTest {
        val repo = FakeStationRepository(stations = listOf(stationResponse("st-1", "Harbour Quay")))
        val vm = StationDetailViewModel(StationDetailInteractor(repo), FakeFavoritesRepository())

        vm.load("st-1")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is StationDetailUiState.Success)
        assertEquals("Harbour Quay", (state as StationDetailUiState.Success).station.name)
    }

    @Test
    fun `missing station emits error`() = runTest {
        val vm = StationDetailViewModel(StationDetailInteractor(FakeStationRepository()), FakeFavoritesRepository())

        vm.load("does-not-exist")
        advanceUntilIdle()

        assertTrue(vm.uiState.value is StationDetailUiState.Error)
    }

    @Test
    fun `toggling favorite updates favorite state`() = runTest {
        val repo = FakeStationRepository(stations = listOf(stationResponse("st-1")))
        val favorites = FakeFavoritesRepository()
        val vm = StationDetailViewModel(StationDetailInteractor(repo), favorites)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.isFavorite.collect {} }

        vm.load("st-1")
        advanceUntilIdle()
        assertFalse(vm.isFavorite.value)

        vm.toggleFavorite()
        advanceUntilIdle()
        assertTrue(vm.isFavorite.value)

        vm.toggleFavorite()
        advanceUntilIdle()
        assertFalse(vm.isFavorite.value)
    }
}

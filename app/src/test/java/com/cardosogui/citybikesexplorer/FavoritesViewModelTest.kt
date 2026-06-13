package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.favorites.FavoritesInteractor
import com.cardosogui.citybikesexplorer.favorites.FavoritesUiState
import com.cardosogui.citybikesexplorer.favorites.FavoritesViewModel
import com.cardosogui.citybikesexplorer.testutil.FakeFavoritesRepository
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
class FavoritesViewModelTest {

    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private val stations = listOf(
        stationResponse("1", "A"),
        stationResponse("2", "B"),
        stationResponse("3", "C"),
    )

    private fun TestScope.collect(vm: FavoritesViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
    }

    private fun content(vm: FavoritesViewModel) = vm.uiState.value as FavoritesUiState.Content

    @Test
    fun `shows only favorited stations`() = runTest {
        val favorites = FakeFavoritesRepository(setOf("2"))
        val vm = FavoritesViewModel(FavoritesInteractor(FakeStationRepository(stations), favorites))
        collect(vm)
        advanceUntilIdle()

        assertEquals(listOf("2"), content(vm).stations.map { it.id })
    }

    @Test
    fun `reacts to a newly favorited station`() = runTest {
        val favorites = FakeFavoritesRepository(setOf("2"))
        val vm = FavoritesViewModel(FavoritesInteractor(FakeStationRepository(stations), favorites))
        collect(vm)
        advanceUntilIdle()

        favorites.toggle("1")
        advanceUntilIdle()

        assertEquals(setOf("1", "2"), content(vm).stations.map { it.id }.toSet())
    }

    @Test
    fun `empty when nothing is favorited`() = runTest {
        val vm = FavoritesViewModel(FavoritesInteractor(FakeStationRepository(stations), FakeFavoritesRepository()))
        collect(vm)
        advanceUntilIdle()

        assertTrue(content(vm).stations.isEmpty())
    }
}

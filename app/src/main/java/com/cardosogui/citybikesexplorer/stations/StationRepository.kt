package com.cardosogui.citybikesexplorer.stations

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.BikesResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import com.cardosogui.citybikesexplorer.data.remote.CityBikesApi
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext

interface StationRepository {
    suspend fun getStations(): StationsResponse
    suspend fun getBikes(stationId: String): BikesResponse
}

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val api: CityBikesApi,
    private val dispatcher: CoroutineDispatcher,
) : StationRepository {

    override suspend fun getStations(): StationsResponse = withContext(dispatcher) {
        api.getStations()
    }

    override suspend fun getBikes(stationId: String): BikesResponse = withContext(dispatcher) {
        api.getBikes(stationId)
    }
}

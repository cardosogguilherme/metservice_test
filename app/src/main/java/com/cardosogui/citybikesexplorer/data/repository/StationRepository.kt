package com.cardosogui.citybikesexplorer.data.repository

import com.cardosogui.citybikesexplorer.data.model.BikeResponse
import com.cardosogui.citybikesexplorer.data.model.StationResponse
import com.cardosogui.citybikesexplorer.data.remote.CityBikesApi
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface StationRepository {
    suspend fun getStations(): List<StationResponse>
    suspend fun getBikes(stationId: String): List<BikeResponse>
}

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val api: CityBikesApi,
    private val dispatcher: CoroutineDispatcher,

) : StationRepository {

    override suspend fun getStations(): List<StationResponse> = withContext(dispatcher) {
        api.getStations().stationResponses
    }

    override suspend fun getBikes(stationId: String): List<BikeResponse> = withContext(dispatcher) {
        api.getBikes(stationId).bikes
    }
}

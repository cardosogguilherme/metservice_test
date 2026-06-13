package com.cardosogui.citybikesexplorer.data.repository

import com.cardosogui.citybikesexplorer.data.model.Station
import com.cardosogui.citybikesexplorer.data.remote.CityBikesApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface StationRepository {
    suspend fun getStations(): List<Station>
}

@Singleton
class StationRepositoryImpl @Inject constructor(
    private val api: CityBikesApi,
) : StationRepository {

    override suspend fun getStations(): List<Station> = withContext(Dispatchers.IO) {
        api.getStations().stations
    }
}

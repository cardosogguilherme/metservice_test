package com.cardosogui.citybikesexplorer.data.remote

import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import retrofit2.http.GET

interface CityBikesApi {

    @GET("stations")
    suspend fun getStations(): StationsResponse
}

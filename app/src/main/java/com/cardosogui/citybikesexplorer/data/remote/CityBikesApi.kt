package com.cardosogui.citybikesexplorer.data.remote

import com.cardosogui.citybikesexplorer.data.model.BikesResponse
import com.cardosogui.citybikesexplorer.data.model.StationsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CityBikesApi {

    @GET("stations")
    suspend fun getStations(): StationsResponse

    @GET("stations/{stationId}/bikes")
    suspend fun getBikes(@Path("stationId") stationId: String): BikesResponse
}

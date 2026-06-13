package com.cardosogui.citybikesexplorer.data.remote

import com.cardosogui.citybikesexplorer.data.model.RideAckResponse
import retrofit2.http.POST

interface RideApi {

    @POST("ride/initiate")
    suspend fun initiateRide(): RideAckResponse

    @POST("ride/finish")
    suspend fun finishRide(): RideAckResponse
}

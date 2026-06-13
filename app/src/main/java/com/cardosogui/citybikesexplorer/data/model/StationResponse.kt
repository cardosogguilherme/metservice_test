package com.cardosogui.citybikesexplorer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationsResponse(
    val stationResponses: List<StationResponse>,
)

@Serializable
data class StationResponse(
    val id: String,
    val name: String,
    @SerialName("free_bikes") val freeBikes: Int,
    @SerialName("empty_slots") val emptySlots: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val lastUpdated: String,
    val distanceKm: Double,
    val imageLink: String,
)

@Serializable
data class BikesResponse(
    val bikes: List<BikeResponse>,
)

@Serializable
data class BikeResponse(
    val id: String,
    val name: String,
    val stationId: String?,
    val batteryPercent: Int,
)

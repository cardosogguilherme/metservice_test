package com.cardosogui.citybikesexplorer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationsResponse(
    val stations: List<Station>,
)

@Serializable
data class Station(
    val id: String,
    val name: String,
    @SerialName("free_bikes") val freeBikes: Int,
    @SerialName("empty_slots") val emptySlots: Int,
    val latitude: Double,
    val longitude: Double,
)

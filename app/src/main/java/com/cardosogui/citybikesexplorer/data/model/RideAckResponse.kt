package com.cardosogui.citybikesexplorer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RideAckResponse(
    val status: String,
)

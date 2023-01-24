package io.ipolyzos.models

import kotlinx.serialization.Serializable

@Serializable
data class SensorInfo(
    val id: String,
    val latitude: String,
    val longitude: String,
    val sensorType: String,
    val generation: Int,
    val deployed: Long
)
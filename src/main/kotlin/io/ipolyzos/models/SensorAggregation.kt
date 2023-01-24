package io.ipolyzos.models

import kotlinx.serialization.Serializable

@Serializable
data class SensorAggregation(
    val sensorId: String,
    val startTime: String,
    val endTime: String,
    val readings: List<Double>,
    val averageTemp: Double
)

package io.ipolyzos.models

import kotlinx.serialization.Serializable

@Serializable
data class SensorReading(val sensorId: String, val reading: Double, val timestamp: Long)

package io.ipolyzos.utils

import com.github.javafaker.Faker
import io.ipolyzos.models.SensorInfo
import io.ipolyzos.models.SensorReading
import java.sql.Timestamp
import java.util.Random
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object DataGenerator {
    private val faker = Faker()
    private val random = Random()
    private val sensorIdCounter = AtomicInteger(1)
    private val types: List<String> = listOf(
        "TEMPERATURE"
//        "PROXIMITY"
    )

    fun generateSensorReading(maxId: Int): SensorReading {
        Thread.sleep(random.nextInt(500).toLong())
        return SensorReading(
            sensorId = faker.number().numberBetween(0, maxId).toString(),
            reading = ((40 + random.nextGaussian()) * 10).toInt() / 10.0, //faker.number().randomDouble(2, 1, 45),
            timestamp = Timestamp(System.currentTimeMillis() - 1000000).time
        )
    }

    fun getSensors(maxIds: Int): Sequence<SensorInfo> {
        return (0 until maxIds)
            .asSequence()
            .map { generateSensorInfo() }
    }

    private fun generateSensorInfo(): SensorInfo {
        return SensorInfo(
            sensorId = (sensorIdCounter.getAndIncrement()).toString(),
            latitude = faker.address().latitude(),
            longitude = faker.address().longitude(),
            sensorType = types[faker.number().numberBetween(0, types.size)],
            generation = faker.number().numberBetween(0, 4),
            deployed = Timestamp(faker.date().past(1000, TimeUnit.DAYS).time).time
        )
    }
}
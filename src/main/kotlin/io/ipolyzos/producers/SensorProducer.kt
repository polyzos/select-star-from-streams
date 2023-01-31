package io.ipolyzos.producers

import io.confluent.kafka.serializers.KafkaJsonSerializer
import io.ipolyzos.models.SensorInfo
import io.ipolyzos.models.SensorReading
import mu.KLogger
import mu.KotlinLogging
import org.apache.flink.kafka.shaded.org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import io.ipolyzos.utils.DataGenerator
import java.util.*

fun main() = SensorProducer.runProducer()

object SensorProducer {
    private val logger: KLogger by lazy { KotlinLogging.logger {} }

    private const val sensorNum = 10
    private const val totalSensorReadings = 10000
    private const val sensorInfoTopic = "sensor.info"
    private const val sensorReadingsTopic = "sensor.readings"

    fun runProducer() {
        // Generate 100 sensors
        val sensorInfo = DataGenerator.getSensors(sensorNum)

        val properties = Properties()
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.canonicalName
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaJsonSerializer::class.java.canonicalName
        properties[ProducerConfig.ACKS_CONFIG] = "1"

        logger.info("Starting Kafka Producers  ...")
        val sensorInfoProducer = KafkaProducer<String, SensorInfo>(properties)
        val sensorReadingProducer = KafkaProducer<String, SensorReading>(properties)

        logger.info { "Generating $sensorNum sensor information ..." }
        sensorInfo.forEach { handleMessage(sensorInfoProducer, sensorInfoTopic, it.sensorId, it) }

        logger.info { "Generating $totalSensorReadings sensor readings ..." }
        // Generate 100.000 readings for those sensors
        var count = 0
        for (i in 0 until totalSensorReadings) {
            val reading = DataGenerator.generateSensorReading(sensorNum)
            handleMessage(sensorReadingProducer, sensorReadingsTopic, reading.sensorId, reading)
            count += 1
            if (count % 1000 == 0) {
                logger.info { "Total so far $count." }
            }
        }

        sensorInfoProducer.flush()
        sensorReadingProducer.flush()

        logger.info("Closing Producers ...")
        sensorInfoProducer.close()
        sensorReadingProducer.close()
    }

    private fun <K, V> handleMessage(producer: Producer<K, V>, topic: String, key: K, value: V) {
        ProducerRecord(topic, key, value)
            .also { record ->
                producer.send(record) { metadata, exception ->
                    exception?.let {
                        logger.error { "Error while producing: $exception" }
                    } ?: kotlin.run {
                        logger.info { "Successfully stored offset '${metadata.offset()}': partition: ${metadata.partition()} - ${metadata.topic()} " }
                    }
                }
            }
    }
}
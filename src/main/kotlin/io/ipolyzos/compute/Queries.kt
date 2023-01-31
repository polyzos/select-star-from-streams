package io.ipolyzos.compute

object Queries {
    val CREATE_SENSORS_TABLE = """
        CREATE TABLE sensors (
            sensorId STRING,
            latitude String,
            longitude String,
            sensorType STRING,
            generation STRING,
            deployed BIGINT
       ) WITH (
            'connector' = 'kafka',
            'topic' = 'sensor.info',
            'properties.bootstrap.servers' = 'localhost:9092',
            'properties.group.id' = 'group.sensor.info',
            'format' = 'json',
            'scan.startup.mode' = 'earliest-offset',
            'json.timestamp-format.standard' = 'ISO-8601',
            'json.fail-on-missing-field' = 'false',
            'json.ignore-parse-errors' = 'true'
        )
    """.trimIndent()

    val CREATE_READINGS_TABLE = """
        CREATE TABLE readings (
            sensorId STRING,
            reading DOUBLE,
            eventTime_ltz AS TO_TIMESTAMP_LTZ(`timestamp`, 3),
            `ts` TIMESTAMP(3) METADATA FROM 'timestamp',
            `timestamp` BIGINT,
                WATERMARK FOR eventTime_ltz AS eventTime_ltz - INTERVAL '30' SECONDS
       ) WITH (
            'connector' = 'kafka',
            'topic' = 'sensor.readings',
            'properties.bootstrap.servers' = 'localhost:9092',
            'properties.group.id' = 'group.sensor.readings',
            'format' = 'json',
            'scan.startup.mode' = 'earliest-offset',
            'json.timestamp-format.standard' = 'ISO-8601',
            'json.fail-on-missing-field' = 'false',
            'json.ignore-parse-errors' = 'true'
        )
    """.trimIndent()

    val SELECT_SENSOR_INFORMATION = """
        SELECT * FROM sensors
    """.trimIndent()

    val SELECT_SENSOR_READINGS = """
        SELECT * FROM readings
    """.trimIndent()

    val PROJECT_WITH_FILTER_QUERY = """
        SELECT  sensorId, reading, eventTime_ltz
        FROM    readings
        WHERE   reading > 40
    """.trimIndent()

    val READINGS_PER_SENSOR_QUERY = """
        SELECT id, COUNT(id)
        FROM readings
        GROUP BY id
    """.trimIndent()

    val AVERAGE_READING_PER_MINUTE_PER_SENSOR = """
        SELECT
          sensorId,
          window_start,
          window_end,
          COUNT(reading) AS totalReadings,
          LISTAGG(CAST(reading AS STRING)) AS readingsList,
          ROUND(AVG(reading),1) as averageReading
        FROM TABLE(TUMBLE(TABLE readings, DESCRIPTOR(eventTime_ltz), INTERVAL '1' MINUTE))
        GROUP BY sensorId, window_start, window_end
    """.trimIndent()

    val JOIN_SENSOR_READINGS_WITH_INFO_QUERY = """
        SELECT 
            sensors.sensorId, 
            reading,
            eventTime_ltz,
            latitude,
            longitude,
            sensorType
        FROM readings
            JOIN sensors ON readings.sensorId = sensors.sensorId
    """.trimIndent()
}
package io.ipolyzos.compute

object Queries {
    val CREATE_SENSORS_TABLE = """
        CREATE TABLE sensors (
            id STRING,
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
            id STRING,
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
}
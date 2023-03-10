package io.ipolyzos.compute

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.configuration.Configuration
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class EnrichmentStream {
    private val checkpointsDir  = "file://${System.getProperty("user.dir")}/checkpoints/"
    private val rocksDBStateDir = "file://${System.getProperty("user.dir")}/state/rocksdb/"

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EnrichmentStream().runStream()
        }
    }

    fun runStream() {
        val environment = StreamExecutionEnvironment
            .createLocalEnvironmentWithWebUI(Configuration())

        environment.parallelism = 3

        // Checkpoint Configurations
        environment.enableCheckpointing(5000)
        environment.checkpointConfig.minPauseBetweenCheckpoints = 100
        environment.checkpointConfig.setCheckpointStorage(checkpointsDir)

        val stateBackend = EmbeddedRocksDBStateBackend()
        stateBackend.setDbStoragePath(rocksDBStateDir)
        environment.stateBackend = stateBackend

        environment.checkpointConfig.externalizedCheckpointCleanup =
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION

        // Configure Restart Strategy
        environment.restartStrategy = RestartStrategies.fixedDelayRestart(5, Time.seconds(5))

        val tableEnvironment = StreamTableEnvironment.create(environment)

        // Run some SQL queries to check the existing Catalogs, Databases and Tables
        tableEnvironment
            .executeSql("SHOW CATALOGS")
            .print()

        tableEnvironment
            .executeSql("SHOW DATABASES")
            .print()

        tableEnvironment
            .executeSql("SHOW TABLES")
            .print()

        tableEnvironment
            .executeSql(Queries.CREATE_SENSORS_TABLE)
            .print()

        tableEnvironment
            .executeSql(Queries.CREATE_READINGS_TABLE)
            .print()

        tableEnvironment
            .executeSql("SHOW TABLES")
            .print()

        tableEnvironment
            .executeSql(Queries.JOIN_SENSOR_READINGS_WITH_INFO_QUERY)
            .print()
    }
}
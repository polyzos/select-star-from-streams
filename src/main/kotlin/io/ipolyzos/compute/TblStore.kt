package io.ipolyzos.compute

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment


fun main() = TblStore.runStore()

object TblStore {
    fun runStore() {
        val warehousePath  = "file://${System.getProperty("user.dir")}/warehouse"
        val checkpointsDir  = "file://${System.getProperty("user.dir")}/checkpoints/"

        val environment = StreamExecutionEnvironment
            .createLocalEnvironmentWithWebUI(Configuration())

        environment.parallelism = 1

        // Checkpoint Configurations
        environment.enableCheckpointing(10000)
        environment.checkpointConfig.minPauseBetweenCheckpoints = 100
        environment.checkpointConfig.setCheckpointStorage(checkpointsDir)

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


        val query = """
             CREATE CATALOG tblCatalog WITH (
                'type'='table-store',
                'warehouse'='file:/tmp/table_store'
                'file.format' = 'parquet'
            );
        """.trimIndent()
        println(query)
        tableEnvironment.executeSql(query)

        tableEnvironment
            .executeSql("SHOW CATALOGS")
            .print()

        tableEnvironment
            .executeSql(Queries.CREATE_READINGS_TABLE)
            .print()

        tableEnvironment
            .executeSql("USE CATALOG tblCatalog")
            .print()

        tableEnvironment.executeSql("""
            CREATE TABLE sensor_count (
                sensorId STRING PRIMARY KEY NOT ENFORCED,
                readingsCount BIGINT
            );
        """.trimIndent())


        tableEnvironment
            .executeSql("SHOW TABLES")
            .print()


        tableEnvironment
            .executeSql("""
                INSERT INTO sensor_count
                SELECT sensorId, COUNT(reading) as readingsCount
                FROM `default_catalog`.`default_database`.`readings`
                GROUP BY sensorId
                """.trimIndent())
    }
}
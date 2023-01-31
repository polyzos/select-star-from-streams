import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kafkaVersion: String by project
val flinkVersion: String by project
val logbackVersion: String by project
val confluentJsonVersion: String by project
val kloggingVersion: String by project
val serializationVersion: String by project
val fakerVersion: String by project
val rocksDBVersion: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "io.ipolyzos"
version = "0.1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("io.confluent:kafka-json-serializer:$confluentJsonVersion")
    implementation("org.rocksdb:rocksdbjni:$rocksDBVersion")

//    implementation("org.apache.flink:flink-clients:$flink_version")
    implementation("org.apache.flink:flink-runtime-web:$flinkVersion")
//    implementation("org.apache.flink:flink-connector-kafka:$flink_version")
    implementation("org.apache.flink:flink-statebackend-rocksdb:$flinkVersion")
    implementation("org.apache.flink:flink-json:$flinkVersion")

    implementation("org.apache.flink:flink-table-api-java-bridge:$flinkVersion")
    implementation("org.apache.flink:flink-table-planner_2.12:$flinkVersion")

    implementation("org.apache.flink:flink-sql-connector-kafka:$flinkVersion")

    implementation("io.github.microutils:kotlin-logging:$kloggingVersion")
    implementation("com.github.javafaker:javafaker:$fakerVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    from(
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    )
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
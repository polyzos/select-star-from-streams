import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kafka_version: String by project
val flink_version: String by project
val logback_version: String by project
val confluent_json_version: String by project
val klogging_version: String by project
val serialization_version: String by project
val faker_version: String by project

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
    implementation("org.apache.kafka:kafka-clients:$kafka_version")
    implementation("io.confluent:kafka-json-serializer:$confluent_json_version")
    implementation("org.rocksdb:rocksdbjni:7.6.0")

//    implementation("org.apache.flink:flink-clients:$flink_version")
    implementation("org.apache.flink:flink-runtime-web:$flink_version")
//    implementation("org.apache.flink:flink-connector-kafka:$flink_version")
    implementation("org.apache.flink:flink-statebackend-rocksdb:$flink_version")
    implementation("org.apache.flink:flink-json:$flink_version")

    implementation("org.apache.flink:flink-table-api-java-bridge:$flink_version")
    implementation("org.apache.flink:flink-table-planner_2.12:$flink_version")

    implementation("org.apache.flink:flink-sql-connector-kafka:$flink_version")

    implementation("io.github.microutils:kotlin-logging:$klogging_version")
    implementation("com.github.javafaker:javafaker:$faker_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

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
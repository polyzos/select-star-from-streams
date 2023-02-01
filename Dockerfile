FROM flink:latest
ADD jars/flink-sql-connector-kafka-1.16.0.jar /opt/flink/lib/

#RUN wget -P /opt/flink/lib https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka_2.12/1.17-SNAPSHOT/flink-sql-connector-kafka_scala_2.12-1.17-SNAPSHOT.jar

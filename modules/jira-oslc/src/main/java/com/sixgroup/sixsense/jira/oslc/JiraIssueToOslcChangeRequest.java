package com.sixgroup.sixsense.jira.oslc;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;
import za.co.absa.abris.config.AbrisConfig;
import za.co.absa.abris.config.FromAvroConfig;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static za.co.absa.abris.avro.functions.from_avro;

public class JiraIssueToOslcChangeRequest {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("jira-oslc")
                .getOrCreate();

        spark.sparkContext()
                .setLogLevel("ERROR");

        FromAvroConfig fromAvroConfig = AbrisConfig
                .fromConfluentAvro()
                .downloadReaderSchemaByLatestVersion()
                .andTopicNameStrategy(System.getenv("KAFKA_JIRA_ISSUE_TOPIC"), false)
                .usingSchemaRegistry(System.getenv("SCHEMA_REGISTRY_URL"));

        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"))
                .option("subscribe", System.getenv("KAFKA_JIRA_ISSUE_TOPIC"))
                .option("startingOffsets", "earliest")
                .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                .load();

        Dataset<Row>output = df.select(from_avro(col("value"), fromAvroConfig).alias("value"));
        Dataset<String> json = output.toJSON();

        json
            .selectExpr("CAST(value AS STRING)")
            .writeStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"))
            .option("topic", System.getenv("KAFKA_OSLC_CHANGE_REQUEST_TOPIC"))
            .option("checkpointLocation", System.getenv("CHECKPOINT_LOCATION"))
            .start()
            .awaitTermination();

    }
}
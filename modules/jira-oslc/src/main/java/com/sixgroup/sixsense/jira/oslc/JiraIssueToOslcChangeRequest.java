package com.sixgroup.sixsense.jira.oslc;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import static za.co.absa.abris.avro.functions.from_avro;

import static org.apache.spark.sql.functions.col;

import za.co.absa.abris.config.AbrisConfig;
import za.co.absa.abris.config.FromAvroConfig;

public class JiraIssueToOslcChangeRequest {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName(System.getProperty("JiraIssueToOslcChangeRequest"))
                .getOrCreate();

        spark.sparkContext()
                .setLogLevel("ERROR");

        FromAvroConfig fromAvroConfig = AbrisConfig
                .fromConfluentAvro()
                .downloadReaderSchemaByLatestVersion()
                .andTopicNameStrategy(System.getProperty("KAFKA_TOPIC"), false)
                .usingSchemaRegistry(System.getProperty("SCHEMA_REGISTRY_URL"));

        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getProperty("BOOTSTRAP_SERVER"))
                .option("subscribe", System.getProperty("KAFKA_JIRA_ISSUE_TOPIC"))
                .option("startingOffsets", "earliest")
                .option("kafka.group.id", System.getProperty("KAFKA_GROUP_ID"))
                .load();

        df = df.select(from_avro(col("value"), fromAvroConfig)).as("ConnectDefault");

        df.selectExpr("topic", "CAST(key AS STRING)", "CAST(value AS STRING)")
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getProperty("BOOTSTRAP_SERVER"))
                .option("topic", System.getProperty("KAFKA_OSLC_CHANGE_REQUEST_TOPIC"))
                .start();
    }

}
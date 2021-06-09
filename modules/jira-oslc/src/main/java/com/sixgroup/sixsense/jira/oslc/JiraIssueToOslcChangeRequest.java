package com.sixgroup.sixsense.jira.oslc;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class JiraIssueToOslcChangeRequest {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName(System.getProperty("JiraIssueToOslcChangeRequest"))
                .getOrCreate();

        spark.sparkContext()
                .setLogLevel("ERROR");

        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getProperty("BOOTSTRAP_SERVER"))
                .option("subscribe", System.getProperty("KAFKA_JIRA_ISSUE_TOPIC"))
                .option("startingOffsets", "earliest")
                .option("kafka.group.id", System.getProperty("KAFKA_GROUP_ID"))
                .load();

        df.selectExpr("topic", "CAST(key AS STRING)", "CAST(value AS STRING)")
                .writeStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getProperty("BOOTSTRAP_SERVER"))
                .option("topic", System.getProperty("KAFKA_CHANGE_REQUEST_TOPIC"))
                .start();
    }

}
package com.sixgroup.sixsense.jira;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;
import za.co.absa.abris.config.AbrisConfig;
import za.co.absa.abris.config.FromAvroConfig;

import static org.apache.spark.sql.functions.col;
import static za.co.absa.abris.avro.functions.from_avro;

public class JiraKafkaToJiraDelta {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraKafkaToJiraDelta")
                .getOrCreate();


        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", System.getenv("MINIO_ENDPOINT"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "True");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.sparkContext().setLogLevel("ERROR");

        FromAvroConfig abrisConfig = AbrisConfig
                .fromConfluentAvro()
                .downloadReaderSchemaByLatestVersion()
                .andTopicNameStrategy(System.getenv("KAFKA_JIRA_ISSUE_TOPIC"), false)
                .usingSchemaRegistry(System.getenv("SCHEMA_REGISTRY_URL"));

        try {
            spark
                    .readStream()
                    .format("kafka")
                    .option("kafka.bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"))
                    .option("subscribe", System.getenv("KAFKA_JIRA_ISSUE_TOPIC"))
                    .option("startingOffsets", "earliest")
                    .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                    .option("mergeSchema", "true")
                    .load()
                    .select(from_avro(col("value"), abrisConfig).as("value"))
                    .select(col("value.data.*"))
                    .writeStream()
                    .format("delta")
                    .outputMode("append")
                    .option("mergeSchema", "true")
                    .option("checkpointLocation", System.getenv("CHECKPOINT_LOCATION"))
                    .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("KAFKA_JIRA_ISSUE_TOPIC"))
                    .awaitTermination();
        } catch (StreamingQueryException e) {
            throw new RuntimeException(e);
        }
    }

}

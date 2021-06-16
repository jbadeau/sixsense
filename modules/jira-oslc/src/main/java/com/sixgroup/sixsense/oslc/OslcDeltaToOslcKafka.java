package com.sixgroup.sixsense.oslc;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

public class OslcDeltaToOslcKafka {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("OslcDeltaToOslcKafka")
                .getOrCreate();

        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", System.getenv("MINIO_ENDPOINT"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "True");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.sparkContext().setLogLevel("ERROR");

        try {
            spark
                    .readStream()
                    .format("delta")
                    .load(System.getenv("MINIO_BUCKET") + System.getenv("KAFKA_OSLC_CHANGE_REQUEST_TOPIC"))
                    .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                    .writeStream()
                    .format("kafka")
                    .outputMode(OutputMode.Append())
                    .option("kafka.bootstrap.servers", System.getenv("BOOTSTRAP_SERVER"))
                    .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                    .option("topic", System.getenv("KAFKA_OSLC_CHANGE_REQUEST_TOPIC"))
                    .option("checkpointLocation", System.getenv("CHECKPOINT_LOCATION"))
                    .start()
                    .awaitTermination();
        } catch (StreamingQueryException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}

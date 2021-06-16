package com.sixgroup.sixsense.oslc;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryException;

public class JiraDeltaToOslcDelta {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraDeltaToOslcDelta")
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
                    .load(System.getenv("MINIO_BUCKET") + System.getenv("KAFKA_JIRA_ISSUE_TOPIC"))
                    .writeStream()
                    .format("delta")
                    .outputMode("append")
                    .option("checkpointLocation", System.getenv("CHECKPOINT_LOCATION"))
                    .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("KAFKA_OSLC_CHANGE_REQUEST_TOPIC"))
                    .awaitTermination();
        } catch (StreamingQueryException e) {
            throw new RuntimeException(e);
        }
    }

}

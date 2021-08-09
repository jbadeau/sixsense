package com.sixgroup.sixsense.pipeline.jira.issue;

import com.sixgroup.sixsense.pipeline.jira.issue.domain.JiraIssueSchema;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class JiraIssuesKafkaSource {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraIssuesKafkaSource")
                .getOrCreate();


        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", "http://" + System.getenv("MINIO_SERVICE_HOST") + ":" + System.getenv("MINIO_SERVICE_PORT"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "true");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.connection.ssl.enabled", "false");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.sparkContext().setLogLevel("ERROR");

        //local
//        spark.sparkContext().hadoopConfiguration().set("fs.s3a.multipart.size", "104857600");
//        spark.sparkContext().hadoopConfiguration().set("fs.s3a.multipart.threshold", "2147483647");

        spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVER"))
                .option("subscribe", System.getenv("KAFKA_TOPIC_SOURCE_JIRA_ISSUES"))
                .option("startingOffsets", "earliest")
                .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                .option("mergeSchema", "true")
                .option("failOnDataLoss", "false")
                .load()

                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"), JiraIssueSchema.SCHEMA).as("issue"))
                .select("issue.*")

                .writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE"))
                .awaitTermination();

                //local
//                .writeStream()
//                .format("console")
//                .option("truncate", "false")
//                .option("mergeSchema", "true")
//                .outputMode(OutputMode.Append())
//                .start()
//                .awaitTermination();
    }
}

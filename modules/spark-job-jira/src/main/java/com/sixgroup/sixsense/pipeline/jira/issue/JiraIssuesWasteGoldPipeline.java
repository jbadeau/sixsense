package com.sixgroup.sixsense.pipeline.jira.issue;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class JiraIssuesWasteGoldPipeline {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        SparkSession spark = session();
        context(spark);

        Dataset ds = read(spark);
        ds = transform(ds);
        StreamingQuery q = write(ds);
        q.awaitTermination();
    }

    private static SparkSession session() {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraIssuesWasteGoldPipeline")
                .getOrCreate();
        return spark;
    }

    private static void context(SparkSession spark) {
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", "http://" + System.getenv("MINIO_SERVICE_HOST") + ":" + System.getenv("MINIO_SERVICE_PORT"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "true");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.connection.ssl.enabled", "false");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.sparkContext().setLogLevel("ERROR");
    }

    private static Dataset<Row> read(SparkSession spark) {
        return spark
                .readStream()
                .format("delta")
                .load(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"));
    }

    private static Dataset transform(Dataset<Row> ds) {
        return ds.select(col("key"), col("fields.status.name").as("status"), col("fields.resolution.name").as("resolution"), col("fields.statuscategorychangedate").as("statuscategorychangedate"))
                .withColumn("measurement", lit("waste"))
                .withColumn("timestamp", unix_timestamp(col("statuscategorychangedate"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .filter(col("resolution").equalTo("Withdrawn").or(col("resolution").equalTo("Cancelled")))
                .select(to_json(struct(col("measurement"), col("key"), col("status"), col("resolution"), col("timestamp"))).alias("value"));
    }

    private static StreamingQuery write(Dataset<Row> ds) throws TimeoutException {
        return ds.writeStream()
                .format("kafka")
                .outputMode(OutputMode.Append())
                .option("kafka.bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVER"))
                .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                .option("topic", System.getenv("KAFKA_TOPIC_JIRA_ISSUES"))
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .start();
    }

}
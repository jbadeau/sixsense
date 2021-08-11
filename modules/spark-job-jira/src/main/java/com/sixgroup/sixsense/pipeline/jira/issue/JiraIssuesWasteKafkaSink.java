package com.sixgroup.sixsense.pipeline.jira.issue;

import com.sixgroup.sixsense.pipeline.jira.issue.domain.JiraIssueSchema;
import io.delta.tables.DeltaTable;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class JiraIssuesWasteKafkaSink {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraIssuesWasteKafkaSink")
                .getOrCreate();

        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", "http://" + System.getenv("MINIO_SERVICE_HOST") + ":" + System.getenv("MINIO_SERVICE_PORT"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "true");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.connection.ssl.enabled", "false");
        spark.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        spark.sparkContext().setLogLevel("ERROR");

        String source = System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE");

        if (!DeltaTable.isDeltaTable(source)) {
            spark.createDataFrame(spark.emptyDataFrame().rdd(), JiraIssueSchema.SCHEMA)
                    .write()
                    .format("delta")
                    .mode(SaveMode.Overwrite)
                    .save(source);
        }

        Dataset dataset = spark
                .readStream()
                .format("delta")
                .load(source)

                .select(
                        col("key").as("key"),
                        col("status.name").as("status"),
                        col("resolution.name").as("resolution"),
                        col("updateDate").as("updateDate"))

                .withColumn("measurement", lit("waste"))
                .withColumn("reformat", col("updateDate").divide(1000).cast("timestamp"))
                .withColumn("timestamp", date_trunc("month", col("reformat")))
                .select("measurement", "key", "status", "resolution", "updateDate", "timestamp")

                //TODO: filter where resolution.name in list[Cancelled, Withdrawn]
                .select("measurement", "key", "status", "timestamp");

        dataset.registerTempTable("WASTE");
        Dataset<Row> sql = dataset.sqlContext()
                .sql("SELECT measurement, key, status, UNIX_MILLIS(timestamp) AS timestamp FROM WASTE");

        sql.select(
                (col("key")).alias("key"),
                to_json(struct(col("measurement"), col("key"), col("status"), col("timestamp"))).alias("value"))
                .writeStream()
                .format("kafka")
                .outputMode(OutputMode.Append())
                .option("kafka.bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVER"))
                .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                .option("topic", System.getenv("KAFKA_TOPIC_JIRA_ISSUES"))
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .start()
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

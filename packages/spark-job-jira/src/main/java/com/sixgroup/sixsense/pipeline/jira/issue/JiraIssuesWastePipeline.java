package com.sixgroup.sixsense.pipeline.jira.issue;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.commons.io.IOUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.zalando.spark.jsonschema.SchemaConverter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.struct;

public class JiraIssuesWastePipeline {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = session();
        context(spark);

        try {
            Dataset<Row> ds = readBronze(spark);
            ds = transformBronze(ds);
            StreamingQuery q = writeBronze(ds);

            ds = readSilver(spark);
            ds = transformSilver(ds);
            q = writeSilver(ds);

            ds = readGold(spark);
            ds = transformGold(ds);
            q = writeGold(ds);

            q.awaitTermination();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private static SparkSession session() {
        return SparkSession.builder()
                .master("local")
                .appName("JiraIssuesWastePipeline")
                .getOrCreate();
    }

    private static void context(SparkSession session) {
        session.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", "http://" + System.getenv("MINIO_SERVICE_HOST") + ":" + System.getenv("MINIO_SERVICE_PORT"));
        session.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", System.getenv("MINIO_ACCESS_KEY"));
        session.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", System.getenv("MINIO_SECRET_KEY"));
        session.sparkContext().hadoopConfiguration().set("fs.s3a.path.style.access", "true");
        session.sparkContext().hadoopConfiguration().set("fs.s3a.connection.ssl.enabled", "false");
        session.sparkContext().hadoopConfiguration().set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        session.sparkContext().setLogLevel("ERROR");
    }

    private static StructType schemaFromRegistry(String topic) {
        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(System.getenv("SCHEMA_REGISTRY_URL"), 128);
        try {
            String schema = client.getLatestSchemaMetadata(topic).getSchema();
            return SchemaConverter.convertContent(schema);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static StructType schemaFromClasspath(String topic) {
        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(System.getenv("SCHEMA_REGISTRY_URL"), 128);
        try {
            String schema = IOUtils.resourceToString(String.format("/jira/json-schema/%s.json", topic), StandardCharsets.UTF_8);
            return SchemaConverter.convertContent(schema);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Dataset<Row> readBronze(SparkSession spark) {
        return spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", System.getenv("KAFKA_BOOTSTRAP_SERVER"))
                .option("subscribe", System.getenv("KAFKA_TOPIC_SOURCE_JIRA_ISSUES"))
                .option("kafka.group.id", System.getenv("KAFKA_GROUP_ID"))
                .option("mergeSchema", "true")
                .option("failOnDataLoss", "false")
                .load();
    }

    private static Dataset<Row> transformBronze(Dataset<Row> ds) {
        return ds.selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"), schemaFromClasspath(System.getenv("KAFKA_TOPIC_SOURCE_JIRA_ISSUES") + "-value")).as("issue"))
                .select("issue.*");
    }

    private static StreamingQuery writeBronze(Dataset<Row> ds) {
        return ds.writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_BRONZE"));
    }

    private static Dataset<Row> readSilver(SparkSession spark) {
        Dataset<Row> ds = spark
                .readStream()
                .format("delta")
                .load(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_BRONZE"));
        return ds;
    }

    private static Dataset<Row> transformSilver(Dataset<Row> ds) {
        return ds.select(from_json(col("value"), schemaFromClasspath(System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"))).as("issue"))
                .select("issue.*");
    }

    private static StreamingQuery writeSilver(Dataset<Row> ds) {
        return ds.writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"));
    }

    private static Dataset<Row> readGold(SparkSession spark) {
        return spark
                .readStream()
                .format("delta")
                .load(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"));
    }

    private static Dataset transformGold(Dataset<Row> ds) {
        return ds.select(col("key"), col("fields.status.name").as("status"), col("fields.resolution.name").as("resolution"), col("fields.statuscategorychangedate").as("statuscategorychangedate"))
                .withColumn("measurement", lit("waste"))
                .withColumn("timestamp", unix_timestamp(col("statuscategorychangedate"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
                .filter(col("resolution").equalTo("Withdrawn").or(col("resolution").equalTo("Cancelled")))
                .select(to_json(struct(col("measurement"), col("key"), col("status"), col("resolution"), col("timestamp"))).alias("value"));
    }

    private static StreamingQuery writeGold(Dataset<Row> ds) throws TimeoutException {
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
package com.sixgroup.sixsense.pipeline.jira.issue;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.zalando.spark.jsonschema.SchemaConverter;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class JiraIssuesBronzePipeline {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = session();
        context(spark);

        Dataset<Row> ds = read(spark);
        ds = transform(ds);
        StreamingQuery q = write(ds);
        q.awaitTermination();
    }

    private static SparkSession session() {
        return SparkSession.builder()
                .master("local")
                .appName("JiraIssuesBronzePipeline")
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

    private static StructType schema(String topic) {
        CachedSchemaRegistryClient client = new CachedSchemaRegistryClient(System.getenv("SCHEMA_REGISTRY_URL"), 128);
        try {
            String schema = client.getLatestSchemaMetadata(topic).getSchema();
            return SchemaConverter.convertContent(schema);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Dataset<Row> read(SparkSession spark) {
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

    private static Dataset<Row> transform(Dataset<Row> ds) {
        return ds.selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"), schema( System.getenv("KAFKA_TOPIC_SOURCE_JIRA_ISSUES") + "-value")).as("issue"))
                .select("issue.*");
    }

    private static StreamingQuery write(Dataset<Row> ds) {
        return ds.writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_BRONZE"));
    }

}

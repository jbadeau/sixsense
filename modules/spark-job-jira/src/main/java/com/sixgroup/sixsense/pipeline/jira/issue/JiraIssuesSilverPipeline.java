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

public class JiraIssuesSilverPipeline {

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = session();
        context(spark);

        Dataset<Row> ds = read(spark);
        ds = transform(ds);
        StreamingQuery q = write(ds);
        q.awaitTermination();
    }

    private static SparkSession session() {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraIssuesSilverPipeline")
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
        Dataset<Row> ds = spark
                .readStream()
                .format("delta")
                .load(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_BRONZE"));
        return ds;
    }

    private static Dataset<Row> transform(Dataset<Row> ds) {
        return ds.select(from_json(col("value"), schema(System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"))).as("issue"))
                .select("issue.*");
    }

    private static StreamingQuery write(Dataset<Row> ds) {
        return ds.writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE_SILVER"));
    }

}

package com.sixgroup.sixsense.pipeline.jira.issue;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

public class JiraIssuesKafkaSource {

    private static final StructType USER = new StructType()
            .add("emailAddress", StringType, true)
            .add("active", BooleanType, false)
            .add("timezone", StringType, false)
            .add("displayName", StringType, false)
            .add("self", StringType, false)
            .add("accountId", StringType, false)
            .add("name", StringType, true);

    private static final StructType VISIBILITY = new StructType()
            .add("type", StringType, true)
            .add("value", StringType, false);

    private static final StructType ISSUE_TYPE = new StructType()
            .add("id", LongType, false)
            .add("name", StringType, false)
            .add("self", StringType, false)
            .add("iconUri", StringType, false)
            .add("description", StringType, false)
            .add("isSubtask", BooleanType, false);

    private static final StructType STATUS = new StructType()
            .add("id", LongType, false)
            .add("self", StringType, false)
            .add("iconUrl", StringType, false)
            .add("name", StringType, false)
            .add("description", StringType, false)
            .add("statusCategory",
                    new StructType()
                            .add("id", LongType, false)
                            .add("self", StringType, false)
                            .add("key", StringType, false)
                            .add("colorName", StringType, false)
                            .add("name", StringType, false)
            );

    private static final StructType SCHEMA = new StructType()
            .add("id", LongType, false)
            .add("key", StringType, false)
            .add("self", StringType, false)
            .add("summary", StringType, false)
            .add("transitionsUri", StringType, false)
            .add("description", StringType, true)
            .add("creationDate", StringType, true)
            .add("updateDate", StringType, true)
            .add("dueDate", StringType, true)
            .add("status", STATUS)
            .add("issueType", ISSUE_TYPE)
            .add("project",
                    new StructType()
                            .add("id", LongType, false)
                            .add("name", StringType, false)
                            .add("key", StringType, false)
                            .add("self", StringType, false)
            )
            .add("expandos", DataTypes.createArrayType(StringType), true
            )
            .add("components", DataTypes.createArrayType(
                    new StructType()
                            .add("id", LongType, false)
                            .add("name", StringType, false)
                            .add("description", StringType, false)
                            .add("self", StringType, false)
                    ), true
            )
            .add("reporter",
                    USER
                    , true
            )
            .add("assignee",
                    USER
                    , true
            )
            .add("resolution",
                    new StructType()
                            .add("id", LongType, false)
                            .add("name", StringType, false)
                            .add("description", StringType, false)
                            .add("self", StringType, false)
                    , true
            )
//TODO: parsing issueFields is causing schema not being able to be parsed.
//            .add("issueFields", DataTypes.createArrayType(
//                    new StructType()
//                            .add("id", StringType, false)
//                            .add("name", StringType, false)
//                            .add("type", StringType, true)
//                            .add("value", StringType, true)
//                    ), true
//            )
            .add("priority",
                    new StructType()
                            .add("id", LongType, false)
                            .add("self", StringType, false)
                            .add("name", StringType, false)

            )
            .add("votes",
                    new StructType()
                            .add("votes", IntegerType, false)
                            .add("hasVoted", BooleanType, false)
                            .add("self", StringType, false)

            )
            .add("fixVersions", DataTypes.createArrayType(
                    new StructType()
                            .add("id", LongType, false)
                            .add("name", StringType, false)
                            .add("description", StringType, false)
                            .add("self", StringType, false)
                            .add("isArchived", BooleanType, false)
                            .add("isReleased", BooleanType, false)
                            .add("releaseDate", TimestampType, false)
                    ), true
            )
            .add("affectedVersions", DataTypes.createArrayType(
                    new StructType()
                            .add("id", LongType, false)
                            .add("name", StringType, false)
                            .add("description", StringType, false)
                            .add("self", StringType, false)
                            .add("isArchived", BooleanType, false)
                            .add("isReleased", BooleanType, false)
                            .add("releaseDate", TimestampType, false)
                    ), true
            )
            .add("comments", DataTypes.createArrayType(
                    new StructType()
                            .add("id", LongType, false)
                            .add("self", StringType, false)
                            .add("author",
                                    USER
                                    , false)
                            .add("updateAuthor",
                                    USER
                                    , false)
                            .add("creationDate", TimestampType, false)
                            .add("updateDate", TimestampType, false)
                            .add("body", StringType, false)
                            .add("visibility",
                                    VISIBILITY
                                    , false
                            )
                    ), true
            )
            .add("issueLinks", DataTypes.createArrayType(
                    new StructType()
                            .add("targetIssueKey", StringType, false)
                            .add("targetIssueUri", StringType, false)
                            .add("issueLinkType",
                                    new StructType()
                                            .add("direction", StringType, true)
                                    , false)
                            .add("name", StringType, false)
                            .add("description", StringType, false)
                    ), true
            )
            .add("attachments", DataTypes.createArrayType(
                    new StructType()
                            .add("filename", StringType, false)
                            .add("self", StringType, false)
                            .add("author",
                                    USER
                                    , false)
                            .add("creationDate", TimestampType, false)
                            .add("size", IntegerType, false)
                            .add("mimeType", StringType, false)
                            .add("contentUri", StringType, false)
                            .add("thumbnailUri", StringType, false)
                    ), true
            )
            .add("worklogs", DataTypes.createArrayType(
                    new StructType()
                            .add("issueUri", StringType, false)
                            .add("self", StringType, false)
                            .add("author",
                                    USER
                                    , false)
                            .add("updateAuthor",
                                    USER
                                    , false)
                            .add("comment", StringType, false)
                            .add("creationDate", TimestampType, false)
                            .add("updateDate", TimestampType, false)
                            .add("startDate", TimestampType, false)
                            .add("minutesSpent", IntegerType, false)
                            .add("mimeType", StringType, false)
                            .add("visibility",
                                    VISIBILITY
                                    , false)
                    ), true
            )
            .add("watchers",
                    new StructType()
                            .add("numWatchers", IntegerType, false)
                            .add("isWatching", BooleanType, false)
                            .add("self", StringType, false)

            )
            .add("timeTracking",
                    new StructType()
                            .add("originalEstimateMinutes", IntegerType, false)
                            .add("remainingEstimateMinutes", IntegerType, false)
                            .add("timeSpentMinutes", IntegerType, false)
                    , true
            )
            .add("subtasks", DataTypes.createArrayType(
                    new StructType()
                            .add("issueKey", StringType, false)
                            .add("issueUri", StringType, false)
                            .add("summary", StringType, false)
                            .add("issueType",
                                    ISSUE_TYPE
                                    , false)
                            .add("status",
                                    STATUS
                                    , false)
                    ), true
            )
            .add("changelog", DataTypes.createArrayType(
                    new StructType()
                            .add("author",
                                    USER
                                    , false)
                            .add("created", TimestampType, false)
                            .add("items", DataTypes.createArrayType(
                                    new StructType()
                                            .add("fieldType", StringType, true)
                                            .add("field", StringType, false)
                                            .add("from", StringType, false)
                                            .add("fromeString", StringType, false)
                                            .add("to", StringType, false)
                                            .add("toString", StringType, false)
                            ), true)
                    ), true
            )
            .add("operations", DataTypes.createArrayType(
                    new StructType()
                            .add("linkGroups", DataTypes.createArrayType(
                                    new StructType()
                                            .add("id", StringType, false)
                                            .add("header", new StructType()
                                                            .add("id", StringType, false)
                                                            .add("label", StringType, false)
                                                            .add("title", StringType, false)
                                                            .add("iconClass", StringType, false)
                                                    , true)
                                            .add("links", DataTypes.createArrayType(
                                                    new StructType()
                                                            .add("id", StringType, false)
                                                            .add("styleClass", StringType, false)
                                                            .add("label", StringType, false)
                                                            .add("title", StringType, false)
                                                            .add("href", StringType, false)
                                                            .add("weight", IntegerType, false)
                                                            .add("iconClass", StringType, false)
                                            ), true)
                                            .add("weight", IntegerType, false)
                            ), true)
                    ), true
            )
            .add("labels", DataTypes.createArrayType(StringType), true);


    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("JiraIssuesKafkaSource")
                .getOrCreate();


        spark.sparkContext().hadoopConfiguration().set("fs.s3a.endpoint", System.getenv("MINIO_ENDPOINT"));
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
                .select(from_json(col("value"), SCHEMA).as("issue"))
                .select("issue.*")

                //local
//                .writeStream()
//                .format("console")
//                .option("truncate", "false")
//                .option("mergeSchema", "true")
//                .outputMode("append")
//                .start()
//                .awaitTermination();

                .writeStream()
                .format("delta")
                .option("mergeSchema", "true")
                .option("checkpointLocation", System.getenv("SPARK_CHECKPOINT_LOCATION"))
                .outputMode(OutputMode.Append())
                //local
//                .option("path", "/tmp/test")
//                .start()
                .start(System.getenv("MINIO_BUCKET") + "/" + System.getenv("DELTA_TABLE_JIRA_ISSUE"))
                .awaitTermination();
    }
}

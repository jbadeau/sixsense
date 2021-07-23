package com.sixgroup.sixsense.pipeline.jira.issue.domain;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.*;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class JiraIssueSchema {
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

    public static final StructType SCHEMA = new StructType()
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

}

package com.sixgroup.sixsense.pipeline.jira.issue.schema;

import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.LongType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class JiraIssueSilverSchema {

    public static final StructType SCHEMA = new StructType()
            .add("expand", StringType, false)
            .add("id", LongType, false)
            .add("self", StringType, false)
            .add("key", StringType, false)
            .add("fields", new StructType()
                    .add("summary", StringType, true), false);

}

package com.sixgroup.sixsense.pipeline.jira.issue.schema;

import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.types.DataTypes.LongType;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class JiraIssueBronzeSchema {

    public static final StructType SCHEMA = new StructType()
            .add("value", StringType, false)
            .add("key", StringType, false)
            .add("timestamp", LongType, false);

}

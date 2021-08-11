package com.sixgroup.sixsense.pipeline.jira.issue.source;


import jdk.jfr.Description;
import org.apache.beam.runners.spark.SparkContextOptions;

public interface JiraIssuesSourcePipelineOptions  extends SparkContextOptions {

    @Description("fs.s3a.endpoint")
    String getFsS3aEndpoint();

    void setFsS3aEndpoint(String value);

    @Description("fs.s3a.access.key")
    String getFsS3aAccessKey();

    void setFsS3aAccessKey(String value);

    @Description("fs.s3a.secret.key")
    String getFsS3aSecretKey();

    void setFsS3aSecretKey(String value);

    @Description("fs.s3a.path.style.access")
    String getFsS3aPathStyleAccess();

    void setFsS3aPathStyleAccess(String value);

    @Description("fs.s3a.connection.ssl.enabled")
    String getFsS3aConnectionSslEnabled();

    void setFsS3aConnectionSslEnabled(String value);

    @Description("fs.s3a.impl")
    String getFsS3aImpl();

    void setFsS3aImpl(String value);

    @Description("logLevel")
    String getLogLevel();

    void setLogLevel(String value);

    @Description("Jira source topic")
    String getKafkaTopicJiraSource();

    void setKafkaTopicJiraSource(String value);

    @Description("kafka.bootstrap.servers")
    String getKafkaBootstrapServers();

    void setKafkaBootstrapServers(String value);

}
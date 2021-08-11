package com.sixgroup.sixsense.pipeline.jira.issue.source;

import org.apache.avro.generic.GenericRecord;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.parquet.ParquetIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

public class JiraIssuesSourcePipeline {

    public static void main(String[] args) {
        JiraIssuesSourcePipelineOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(JiraIssuesSourcePipelineOptions.class);

        options.setProvidedSparkContext(JavaSparkContext.fromSparkContext(createSparkContext(options)));

        Pipeline pipeline = Pipeline.create(options);

        pipeline.apply(KafkaIO.<Long, String>read()
                .withBootstrapServers(options.getKafkaBootstrapServers())
                .withTopic(options.getKafkaTopicJiraSource())
                .withKeyDeserializer(LongDeserializer.class)
                .withValueDeserializer(StringDeserializer.class));

    }

    private static SparkContext createSparkContext(JiraIssuesSourcePipelineOptions options) {
        SparkContext context = SparkContext.getOrCreate();
        context.hadoopConfiguration().set("fs.s3a.endpoint", options.getFsS3aEndpoint());
        context.hadoopConfiguration().set("fs.s3a.access.key", options.getFsS3aAccessKey());
        context.hadoopConfiguration().set("fs.s3a.secret.key", options.getFsS3aSecretKey());
        context.hadoopConfiguration().set("fs.s3a.path.style.access", options.getFsS3aPathStyleAccess());
        context.hadoopConfiguration().set("fs.s3a.connection.ssl.enabled", options.getFsS3aConnectionSslEnabled());
        context.hadoopConfiguration().set("fs.s3a.impl", options.getFsS3aImpl());
        context.setLogLevel(options.getLogLevel());
        return context;
    }

}

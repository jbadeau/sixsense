package com.sixgroup.sixsense.pipeline.jira.issue.source;


import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarterPipeline {
    private static final Logger LOG = LoggerFactory.getLogger(StarterPipeline.class);

    public static void main(String[] args) {
        Pipeline p = Pipeline.create(
                PipelineOptionsFactory.fromArgs(args).withValidation().create());

        p.apply(Create.of("Hello", "World"))
                .apply(MapElements.via(new SimpleFunction<String, String>() {
                    @Override
                    public String apply(String input) {
                        return input.toUpperCase();
                    }
                }))
                .apply(ParDo.of(new DoFn<String, Void>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        LOG.info(c.element());
                    }
                }));

        p.run();
    }
}
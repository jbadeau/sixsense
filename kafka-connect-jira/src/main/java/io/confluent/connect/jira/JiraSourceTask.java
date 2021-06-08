package io.confluent.connect.jira;

import io.confluent.connect.jira.operation.GetIssues;
import io.confluent.connect.jira.operation.GetProjectCategories;
import io.confluent.connect.jira.operation.GetProjectTypes;
import io.confluent.connect.jira.operation.GetProjects;
import io.confluent.connect.jira.operation.GetRoles;
import io.confluent.connect.jira.operation.GetUsers;
import io.confluent.connect.jira.operation.JiraOperation;
import io.confluent.connect.jira.operation.JiraOperationContext;
import io.confluent.connect.jira.utils.JiraUtils;
import io.confluent.connect.operations.Operation;
import io.confluent.connect.operations.OperationExecutor;
import io.confluent.connect.operations.OperationExecutorListener;
import io.confluent.connect.operations.SimpleOperationExecutorListener;
import io.confluent.connect.operations.http.client.HttpClientConfig;
import io.confluent.connect.operations.http.client.HttpClientFactory;
import io.confluent.connect.operations.rest.RestServiceSourceTask;
import io.confluent.connect.utils.Version;
import io.confluent.connect.utils.collect.Destination;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraSourceTask extends RestServiceSourceTask<JiraSourceConnectorConfig, JiraOperationContext, JiraOperation> {
    private static final Logger log = LoggerFactory.getLogger(JiraSourceTask.class);

    private static final String ENTITY_PARTITION_KEY = "name";

    private JiraSourceConnectorConfig config;

    private OperationExecutor executor;

    private List<String> entityNames;

    private Map<String, Map<String, ?>> partitions;

    private CloseableHttpAsyncClient client;

    protected JiraSourceConnectorConfig createTaskConfig(Map<String, String> settings) {
        return new JiraSourceConnectorConfig(settings);
    }

    public void start(Map<String, String> settings) {
        this.config = createTaskConfig(settings);
        this.entityNames = this.config.entityNames();
        this.partitions = new HashMap<>();
        SimpleOperationExecutorListener<JiraOperation> executorListener = new SimpleOperationExecutorListener();
        this

                .executor = OperationExecutor.create().withMaxBatchSize(this.config.maxBatchSize()).withMaxPollInterval(this.config.maxPollInterval()).withListener((OperationExecutorListener) executorListener).withPriorityFunction(JiraOperationOrder.lowestDelayHighestPriorityEarliestFirst(Duration.ofMillis(10000L))).build();
        this.client = (new HttpClientFactory((HttpClientConfig) this.config)).createClient();
        this.client.start();
        createOperations();
    }

    private void createOperations() {
        OffsetStorageReader offsetReader = this.context.offsetStorageReader();
        List<String> entities = new ArrayList<>(this.entityNames);
        entities.removeAll(JiraUtils.getEntites(JiraEntity.ALL_DEPENDENT_ENTITIES));
        for (String entityName : entities) {
            Map<String, ?> entityPartition = createPartitionFor(entityName);
            Map<String, ?> entityOffset = offsetReader.offset(entityPartition);
            JiraOperationContext operationContext = createOperationContext(this.config, entityName, entityPartition, this.executor

                            .destination(), (HttpAsyncClient) this.client,

                    clock());
            JiraOperation initialOperation = createOperation(entityName, entityOffset, operationContext);
            if (entityName.equalsIgnoreCase(JiraEntity.ISSUES.name().toLowerCase()))
                createPartitionsForDependentEntities(JiraUtils.getEntites(JiraEntity.ISSUE_DEPENDENT_ENTITIES));
            if (entityName.equalsIgnoreCase(JiraEntity.PROJECTS.name().toLowerCase()))
                createPartitionsForDependentEntities(JiraUtils.getEntites(JiraEntity.PROJECT_DEPENDENT_ENTITIES));
            this.executor.submitOperation((Operation) initialOperation);
        }
    }

    private void createPartitionsForDependentEntities(List<String> dependentEntities) {
        this.entityNames.forEach(entity -> {
            if (dependentEntities.contains(entity))
                createPartitionFor(entity);
        });
    }

    protected JiraOperation createOperation(String entityName, Map<String, ?> entityOffset, JiraOperationContext context) {
        GetProjectTypes getProjectTypes = null;
        JiraOperation operation = null;
        switch (JiraEntity.toEnum(entityName)) {
            case PROJECTS:
                operation = new GetProjects(context, entityOffset);
                break;
            case ISSUES:
                operation = new GetIssues(context, entityOffset);
                break;
            case USERS:
                operation = new GetUsers(context, entityOffset);
                break;
            case ROLES:
                operation = new GetRoles(context, entityOffset);
                break;
            case PROJECT_CATEGORIES:
                operation = new GetProjectCategories(context, entityOffset);
                break;
            case PROJECT_TYPES:
                operation = new GetProjectTypes(context, entityOffset);
                break;
        }
        return operation;
    }

    protected JiraOperationContext createOperationContext(JiraSourceConnectorConfig config, String entityName, Map<String, ?> entityPartition, Destination<SourceRecord> destination, HttpAsyncClient client, Time clock) {
        return new JiraOperationContext(config, entityName, config

                .topicName(entityName), entityPartition, destination, this.partitions, client, clock);
    }

    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> result = this.executor.pollForRecords();
        log.debug("Returning {} records", Integer.valueOf(result.size()));
        return (result.size() == 0) ? null : result;
    }

    public String version() {
        return Version.forClass(getClass());
    }

    public void stop() {
        log.info("Stopping task and cancelling any currently running operations");
        try {
            if (this.executor != null)
                this.executor.stop();
        } finally {
            this.executor = null;
            if (this.client != null)
                try {
                    this.client.close();
                } catch (IOException e) {
                    log.error("Unable to close the HTTP async client when this task was stopped: {}", e

                            .getMessage(), e);
                } finally {
                    this.client = null;
                }
        }
    }

    private Map<String, ?> createPartitionFor(String entityName) {
        Map<String, ?> entityPartition = Collections.singletonMap("name", entityName);
        this.partitions.put(entityName, entityPartition);
        return entityPartition;
    }
}

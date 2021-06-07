package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.operations.http.operation.HttpOperationConfig;
import io.confluent.connect.operations.http.operation.HttpOperationContext;
import io.confluent.connect.utils.collect.Destination;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.source.SourceRecord;

public class JiraOperationContext extends HttpOperationContext<JiraSourceConnectorConfig> {
    private final Map<Long, IssueMapper> issueMapperMap;

    private final List<ObjectNode> comments;

    private final List<ObjectNode> changeLogs;

    private final List<ObjectNode> worklogs;

    private Long issueId;

    private LocalDateTime issueUpdatedDate;

    private final Map<Long, ObjectNode> projectMappers;

    private final Map<String, Map<String, ?>> entityPartitions;

    public JiraOperationContext(JiraSourceConnectorConfig config, String entityName, String topicName, Map<String, ?> entityPartition, Destination<SourceRecord> recordQueue, Map<String, Map<String, ?>> partitions, HttpAsyncClient client, Time clock) {
        super(config, entityName, topicName, entityPartition, recordQueue, client, clock);
        this.issueMapperMap = new LinkedHashMap<>();
        this.entityPartitions = partitions;
        this.comments = new CopyOnWriteArrayList<>();
        this.changeLogs = new CopyOnWriteArrayList<>();
        this.worklogs = new CopyOnWriteArrayList<>();
        this.projectMappers = new ConcurrentHashMap<>();
    }

    public Map<String, ?> getPartitionByEntityName(String entityName) {
        return this.entityPartitions.get(entityName);
    }

    public Map<Long, IssueMapper> issueMapperMap() {
        return this.issueMapperMap;
    }

    public List<ObjectNode> comments() {
        return this.comments;
    }

    public List<ObjectNode> changelogs() {
        return this.changeLogs;
    }

    public List<ObjectNode> worklogs() {
        return this.worklogs;
    }

    public Long issueId() {
        return this.issueId;
    }

    public void issueId(Long issueId) {
        this.issueId = issueId;
    }

    public LocalDateTime issueUpdatedDate() {
        return this.issueUpdatedDate;
    }

    public void issueUpdatedDate(LocalDateTime issueUpdatedDate) {
        this.issueUpdatedDate = issueUpdatedDate;
    }

    public Map<Long, ObjectNode> getProjects() {
        return this.projectMappers;
    }
}

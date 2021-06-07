package com.sixgroup.sixsense.kafka.connect.jira.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationContext;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationResult;
import io.confluent.connect.operations.OperationResult;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

public class ProjectResponseParser extends JiraResponseParser {
    public ProjectResponseParser(String entityFieldName) {
        super(entityFieldName);
    }

    public void parseResponse(HttpRequestBase request, HttpEntity responseEntity, OperationResult<JiraOperationContext> result) throws IOException, InterruptedException {
        JiraOperationResult jiraOperationResult = (JiraOperationResult)result;
        ObjectNode response = (ObjectNode)this.mapper.readValue(responseEntity.getContent(), ObjectNode.class);
        jiraOperationResult.response(response);
        List<String> entities = ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).entityNames();
        if (!entities.contains(JiraEntity.VERSIONS.name().toLowerCase())) {
            for (JsonNode project : response.get("values"))
                parseEntity(jiraOperationResult, (ObjectNode)project.deepCopy(), JiraEntity.PROJECTS
                        .name().toLowerCase());
        } else {
            for (JsonNode project : response.get("values"))
                ((JiraOperationContext)jiraOperationResult.context()).getProjects()
                        .put(Long.valueOf(project.get("id").asLong()), project.deepCopy());
        }
    }
}

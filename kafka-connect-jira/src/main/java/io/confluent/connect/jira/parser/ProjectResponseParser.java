package io.confluent.connect.jira.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.jira.operation.JiraOperationContext;
import io.confluent.connect.jira.operation.JiraOperationResult;
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

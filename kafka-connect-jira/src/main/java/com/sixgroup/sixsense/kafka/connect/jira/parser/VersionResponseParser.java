package com.sixgroup.sixsense.kafka.connect.jira.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationContext;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationResult;
import com.sixgroup.sixsense.kafka.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.OperationResult;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

public class VersionResponseParser extends JiraResponseParser {
    public VersionResponseParser(String entityFieldName) {
        super(entityFieldName);
    }

    public void parseResponse(HttpRequestBase request, HttpEntity responseEntity, OperationResult<JiraOperationContext> result) throws IOException, InterruptedException {
        JiraOperationResult jiraOperationResult = (JiraOperationResult)result;
        ObjectNode response = (ObjectNode)this.mapper.readValue(responseEntity.getContent(), ObjectNode.class);
        jiraOperationResult.response(response);
        JsonNode versions = response.get("values");
        for (JsonNode version : versions)
            parseEntity(jiraOperationResult, (ObjectNode)version.deepCopy(), JiraEntity.VERSIONS
                    .name().toLowerCase());
        if (response.get("isLast").asBoolean()) {
            Long projectId = Long.valueOf(Long.parseLong(JiraParserUtils.getIdFromUrl(request.getURI().toString())));
            if (((JiraOperationContext)jiraOperationResult.context()).getProjects().containsKey(projectId)) {
                parseEntity(jiraOperationResult, (ObjectNode)((JiraOperationContext)jiraOperationResult.context()).getProjects().get(projectId), JiraEntity.PROJECTS
                        .name().toLowerCase());
                ((JiraOperationContext)jiraOperationResult.context()).getProjects().remove(projectId);
            }
        }
    }
}

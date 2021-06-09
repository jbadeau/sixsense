package io.confluent.connect.jira.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.operation.IssueMapper;
import io.confluent.connect.jira.operation.JiraOperationContext;
import io.confluent.connect.jira.operation.JiraOperationResult;
import io.confluent.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.OperationResult;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

public class IssueDependentEntitiesResponseParser extends JiraResponseParser {
    public IssueDependentEntitiesResponseParser(String entityFieldName) {
        super(entityFieldName);
    }

    public void parseResponse(HttpRequestBase request, HttpEntity responseEntity, OperationResult<JiraOperationContext> operationResult) throws IOException {
        ObjectNode response = (ObjectNode)this.mapper.readValue(responseEntity.getContent(), ObjectNode.class);
        JiraOperationResult jiraOperationResult = (JiraOperationResult)operationResult;
        jiraOperationResult.response(response);
        String issueId = JiraParserUtils.getIdFromUrl(request.getURI().toString());
        IssueMapper issueMapper = (IssueMapper)((JiraOperationContext)operationResult.context()).issueMapperMap().get(Long.valueOf(Long.parseLong(issueId)));
        switch (JiraEntity.toEnum(this.entity)) {
            case CHANGELOGS:
                addResponseToContext(response.get("values"), ((JiraOperationContext)operationResult.context()).changelogs());
                issueMapper.hasChangelogs(true);
                break;
            case WORKLOGS:
                addResponseToContext(response.get("worklogs"), ((JiraOperationContext)operationResult.context()).worklogs());
                issueMapper.hasWorklogs(true);
                break;
            case ISSUE_COMMENTS:
                addResponseToContext(response.get("comments"), ((JiraOperationContext)operationResult.context()).comments());
                issueMapper.hasComments(true);
                break;
        }
    }

    private List<ObjectNode> addResponseToContext(JsonNode results, List<ObjectNode> resultsInContext) {
        for (JsonNode result : results)
            resultsInContext.add(result.deepCopy());
        return resultsInContext;
    }
}

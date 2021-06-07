package com.sixgroup.sixsense.kafka.connect.jira.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import com.sixgroup.sixsense.kafka.connect.jira.operation.IssueMapper;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationContext;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationResult;
import com.sixgroup.sixsense.kafka.connect.jira.utils.DateUtils;
import com.sixgroup.sixsense.kafka.connect.jira.utils.JiraUtils;
import io.confluent.connect.operations.OperationResult;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

public class IssueResponseParser extends JiraResponseParser {
    public IssueResponseParser(String entityFieldName) {
        super(entityFieldName);
    }

    public void parseResponse(HttpRequestBase request, HttpEntity responseEntity, OperationResult<JiraOperationContext> result) throws IOException, InterruptedException {
        JiraOperationResult jiraOperationResult = (JiraOperationResult)result;
        ObjectNode response = (ObjectNode)this.mapper.readValue(responseEntity.getContent(), ObjectNode.class);
        jiraOperationResult.response(response);
        JsonNode issueNodes = response.get("issues");
        JsonNode lastIssue = issueNodes.get(issueNodes.size() - 1);
        Long lastIssueId = ((JiraOperationContext)result.context()).issueId();
        LocalDateTime lastIssueUpdatedDateTime = ((JiraOperationContext)result.context()).issueUpdatedDate();
        if (lastIssue != null) {
            lastIssueId = Long.valueOf(lastIssue.get("id").asLong());
            lastIssueUpdatedDateTime = DateUtils.getDate(lastIssue.at("/fields/updated").asText());
        }
        parseIssueAndDependentEntities(jiraOperationResult);
        Long previousIssueId = ((JiraOperationContext)result.context()).issueId();
        LocalDateTime previousIssueUpdatedDateTime = ((JiraOperationContext)result.context()).issueUpdatedDate();
        if (previousIssueUpdatedDateTime != null && lastIssueId.equals(previousIssueId) && previousIssueUpdatedDateTime
                .equals(lastIssueUpdatedDateTime)) {
            jiraOperationResult.response(getEmptyIssueNode());
            return;
        }
        ((JiraOperationContext)result.context()).issueId(lastIssueId);
        ((JiraOperationContext)result.context()).issueUpdatedDate(lastIssueUpdatedDateTime);
    }

    private void parseIssueAndDependentEntities(JiraOperationResult result) throws IOException, InterruptedException {
        List<ObjectNode> comments = ((JiraOperationContext)result.context()).comments();
        for (ObjectNode comment : comments)
            parseEntity(result, comment, JiraEntity.ISSUE_COMMENTS.name().toLowerCase());
        ((JiraOperationContext)result.context()).comments().removeAll(comments);
        List<ObjectNode> changeLogs = ((JiraOperationContext)result.context()).changelogs();
        for (ObjectNode changelog : changeLogs)
            parseEntity(result, changelog, JiraEntity.CHANGELOGS.name().toLowerCase());
        ((JiraOperationContext)result.context()).changelogs().removeAll(changeLogs);
        List<ObjectNode> worklogs = ((JiraOperationContext)result.context()).worklogs();
        for (ObjectNode worklog : worklogs)
            parseEntity(result, worklog, JiraEntity.WORKLOGS.name().toLowerCase());
        ((JiraOperationContext)result.context()).worklogs().removeAll(worklogs);
        Map<Long, IssueMapper> issueMapperMap = ((JiraOperationContext)result.context()).issueMapperMap();
        List<Long> listOfEntriesTobeRemoved = new ArrayList<>();
        List<String> entities = ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).entityNames();
        parseIssueTransitionsAndResolutions(result, entities, issueMapperMap, listOfEntriesTobeRemoved);
        for (Long issueId : listOfEntriesTobeRemoved)
            ((JiraOperationContext)result.context()).issueMapperMap().remove(issueId);
    }

    private void parseIssueTransitionsAndResolutions(JiraOperationResult result, List<String> entities, Map<Long, IssueMapper> issueMapperMap, List<Long> listOfEntriesTobeRemoved) throws IOException, InterruptedException {
        for (Long issueId : issueMapperMap.keySet()) {
            IssueMapper issueMapper = issueMapperMap.get(issueId);
            if (issueMapper.hasChangelogs() && issueMapper.hasComments() && issueMapper.hasWorklogs()) {
                if (entities.contains(JiraEntity.ISSUE_TRANSITIONS.name().toLowerCase()))
                    parseTransitions(result, issueMapper.issue().get("transitions").deepCopy());
                JsonNode resolution = issueMapper.issue().get("fields").get("resolution");
                if (!resolution.isNull() && entities
                        .contains(JiraEntity.RESOLUTIONS.name().toLowerCase()))
                    parseEntity(result, (ObjectNode)resolution.deepCopy(), JiraEntity.RESOLUTIONS
                            .name().toLowerCase());
                ObjectNode issue = issueMapper.issue();
                removeDependentNodes(issue);
                parseEntity(result, issue, JiraEntity.ISSUES.name().toLowerCase());
                listOfEntriesTobeRemoved.add(issueId);
            }
        }
    }

    private void removeDependentNodes(ObjectNode issue) {
        for (String nodeName : JiraUtils.getEntites(JiraEntity.ISSUE_DEPENDENT_ENTITIES)) {
            ObjectNode issueFieldsNode = (ObjectNode)issue.get("fields");
            if (issueFieldsNode.has(nodeName)) {
                issueFieldsNode.remove(nodeName);
                continue;
            }
            issue.remove(nodeName);
        }
    }

    private void parseTransitions(JiraOperationResult result, JsonNode transitions) throws IOException, InterruptedException {
        for (JsonNode transition : transitions)
            parseEntity(result, (ObjectNode)transition.deepCopy(), JiraEntity.ISSUE_TRANSITIONS
                    .name().toLowerCase());
    }

    private ObjectNode getEmptyIssueNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode emptyIssue = mapper.createObjectNode();
        emptyIssue.set("issues", (JsonNode)mapper.createArrayNode());
        return emptyIssue;
    }
}

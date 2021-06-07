package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.parser.IssueDependentEntitiesResponseParser;
import com.sixgroup.sixsense.kafka.connect.jira.parser.JiraResponseParser;
import com.sixgroup.sixsense.kafka.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.Operation;
import java.util.List;
import java.util.Map;

public abstract class JiraStaticEntitiesOperation extends JiraOperation {
    JiraStaticEntitiesOperation(JiraOperationContext context, String entityName, String urlPath, Map<String, ?> offset) {
        super(context, entityName, urlPath, offset);
    }

    protected JiraResponseParser createParser(String entityName) {
        return (JiraResponseParser)new IssueDependentEntitiesResponseParser(entityName);
    }

    public void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        JiraOperationResult result = result();
        ObjectNode response = result.response();
        Long startAt = Long.valueOf(response.get("startAt").asLong() + response.get("maxResults").asLong());
        Long totalRecords = Long.valueOf(response.get("total").asLong());
        if (totalRecords.longValue() > startAt.longValue()) {
            result.setNextPageUrl(
                    JiraParserUtils.addQueryParam(this.urlPath, "startAt", String.valueOf(startAt)));
            operations.add(getOperation(this));
        }
    }

    public abstract JiraStaticEntitiesOperation getOperation(JiraStaticEntitiesOperation paramJiraStaticEntitiesOperation);
}

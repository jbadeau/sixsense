package io.confluent.connect.jira.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.jira.parser.JiraResponseParser;
import io.confluent.connect.jira.parser.ProjectResponseParser;
import io.confluent.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.Operation;
import java.util.List;
import java.util.Map;

public class GetProjects extends JiraOperation {
    public GetProjects(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.PROJECTS

                        .name().toLowerCase(),
                getUrl(context), offset);
    }

    protected GetProjects(GetProjects original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .originalOffset());
    }

    private static String getUrl(JiraOperationContext context) {
        return context.baseUrl() + "/project/search";
    }

    protected JiraResponseParser createParser(String entityName) {
        return (JiraResponseParser)new ProjectResponseParser(entityName);
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        JiraOperationResult result = result();
        createVersionOperation(result, operations);
        ObjectNode response = result.response();
        if (!response.get("isLast").asBoolean()) {
            Long startAt = Long.valueOf(response.get("startAt").asLong() + response.get("maxResults").asLong());
            result.setNextPageUrl(
                    JiraParserUtils.addQueryParam(this.urlPath, "startAt", String.valueOf(startAt)));
            operations.add(new GetProjects(this));
        } else {
            result.setNextPageUrl(this.urlPath);
            operations.add((new GetProjects(this)).setDelay(((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).requestInterval()));
        }
    }

    private void createVersionOperation(JiraOperationResult result, List<Operation<JiraOperationContext>> operations) {
        if (!((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).entityNames().contains(JiraEntity.VERSIONS.name().toLowerCase()))
            return;
        JsonNode projects = result.response().get("values");
        for (JsonNode project : projects)
            operations
                    .add(new GetVersion((JiraOperationContext)this.context, result.offsetFromLastRecord(), Long.valueOf(project.get("id").asLong())));
    }
}

package io.confluent.connect.jira.operation;

import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.operations.Operation;
import io.confluent.connect.operations.OperationResult;
import java.util.List;
import java.util.Map;

public class GetProjectTypes extends JiraOperation {
    public GetProjectTypes(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.PROJECT_TYPES

                .name().toLowerCase(), context
                .baseUrl() + "/project/type", offset);
    }

    protected GetProjectTypes(GetProjectTypes original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        OperationResult result = result();
        result.setNextPageUrl(this.urlPath);
        operations.add((new GetProjectTypes(this)).setDelay(((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).requestInterval()));
    }
}

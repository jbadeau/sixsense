package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.operations.Operation;
import io.confluent.connect.operations.OperationResult;
import java.util.List;
import java.util.Map;

public class GetProjectCategories extends JiraOperation {
    public GetProjectCategories(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.PROJECT_CATEGORIES

                .name().toLowerCase(), context
                .baseUrl() + "/projectCategory", offset);
    }

    protected GetProjectCategories(GetProjectCategories original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        OperationResult result = result();
        result.setNextPageUrl(this.urlPath);
        operations.add((new GetProjectCategories(this)).setDelay(((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).requestInterval()));
    }
}

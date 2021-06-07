package com.sixgroup.sixsense.kafka.connect.jira.operation;


import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.operations.Operation;
import io.confluent.connect.operations.OperationResult;
import java.util.List;
import java.util.Map;

public class GetRoles extends JiraOperation {
    public GetRoles(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.ROLES

                .name().toLowerCase(), context
                .baseUrl() + "/role", offset);
    }

    protected GetRoles(GetRoles original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        OperationResult result = result();
        result.setNextPageUrl(this.urlPath);
        operations.add((new GetRoles(this)).setDelay(((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).requestInterval()));
    }
}

package io.confluent.connect.jira.operation;

import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.Operation;
import java.util.List;
import java.util.Map;

public class GetUsers extends JiraOperation {
    public GetUsers(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.USERS

                .name().toLowerCase(), context
                .baseUrl() + "/users/search?maxResults=" + 'd' + "&startAt=0", offset);
    }

    protected GetUsers(GetUsers original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        JiraOperationResult result = result();
        int startAt = result.responseSize();
        result.setNextPageUrl(
                JiraParserUtils.addQueryParam(this.urlPath, "startAt", String.valueOf(startAt)));
        if (startAt != 0) {
            operations.add(new GetUsers(this));
        } else {
            operations.add((new GetUsers(this)).setDelay(((JiraSourceConnectorConfig)((JiraOperationContext)this.context).config()).requestInterval()));
        }
    }
}

package io.confluent.connect.jira.operation;


import io.confluent.connect.jira.JiraEntity;

import java.util.Map;

public class GetWorklogsOperation extends JiraStaticEntitiesOperation {
    public GetWorklogsOperation(JiraOperationContext context, Map<String, ?> offset, Long issueId) {
        super(context, JiraEntity.WORKLOGS

                        .name().toLowerCase(),
                getUrl(context, issueId), offset);
    }

    private static String getUrl(JiraOperationContext context, Long issueId) {
        return context.baseUrl() + "/issue/" + issueId + "/worklog?startAt=0";
    }

    protected GetWorklogsOperation(GetWorklogsOperation original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    public JiraStaticEntitiesOperation getOperation(JiraStaticEntitiesOperation op) {
        return new GetWorklogsOperation(this);
    }
}

package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import java.util.Map;

public class GetCommentsOperation extends JiraStaticEntitiesOperation {
    public GetCommentsOperation(JiraOperationContext context, Map<String, ?> offset, Long issueId) {
        super(context, JiraEntity.ISSUE_COMMENTS

                        .name().toLowerCase(),
                getUrl(context, issueId), offset);
    }

    private static String getUrl(JiraOperationContext context, Long issueId) {
        return context.baseUrl() + "/issue/" + issueId + "/comment?startAt=0";
    }

    protected GetCommentsOperation(GetCommentsOperation original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    public JiraStaticEntitiesOperation getOperation(JiraStaticEntitiesOperation op) {
        return new GetCommentsOperation(this);
    }
}
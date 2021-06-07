package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import java.util.Map;

public class GetChangelogsOperation extends JiraStaticEntitiesOperation {
    public GetChangelogsOperation(JiraOperationContext context, Map<String, ?> offset, Long issueId) {
        super(context, JiraEntity.CHANGELOGS

                        .name().toLowerCase(),
                getUrl(context, issueId), offset);
    }

    private static String getUrl(JiraOperationContext context, Long issueId) {
        return context.baseUrl() + "/issue/" + issueId + "/changelog?startAt=0";
    }

    protected GetChangelogsOperation(GetChangelogsOperation original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    public JiraStaticEntitiesOperation getOperation(JiraStaticEntitiesOperation op) {
        return new GetChangelogsOperation(this);
    }
}

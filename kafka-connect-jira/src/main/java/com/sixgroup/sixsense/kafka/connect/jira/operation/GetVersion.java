package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.parser.JiraResponseParser;
import com.sixgroup.sixsense.kafka.connect.jira.parser.VersionResponseParser;
import java.util.Map;

public class GetVersion extends JiraStaticEntitiesOperation {
    public GetVersion(JiraOperationContext context, Map<String, ?> offset, Long projectId) {
        super(context, JiraEntity.VERSIONS

                        .name().toLowerCase(),
                getUrl(context, projectId), offset);
    }

    private static String getUrl(JiraOperationContext context, Long projectId) {
        return context.baseUrl() + "/project/" + projectId + "/version?startAt=0";
    }

    protected GetVersion(GetVersion original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    protected JiraResponseParser createParser(String entityName) {
        return (JiraResponseParser)new VersionResponseParser(entityName);
    }

    public JiraStaticEntitiesOperation getOperation(JiraStaticEntitiesOperation op) {
        return new GetVersion(this);
    }
}
package io.confluent.connect.jira.operation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.JiraSourceConnectorConfig;
import io.confluent.connect.jira.parser.IssueResponseParser;
import io.confluent.connect.jira.parser.JiraResponseParser;
import io.confluent.connect.jira.utils.DateUtils;
import io.confluent.connect.jira.utils.JiraParserUtils;
import io.confluent.connect.operations.Operation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetIssues extends JiraOperation {
    public GetIssues(JiraOperationContext context, Map<String, ?> offset) {
        super(context, JiraEntity.ISSUES

                        .name().toLowerCase(),
                getUrl(context, offset), offset);
    }

    protected GetIssues(GetIssues original) {
        super((JiraOperationContext)original.context, original.name, original

                .result().nextPageUrl(), original
                .result().offsetFromLastRecord());
    }

    private static String getUrl(JiraOperationContext context, Map<String, ?> offset) {
        String updatedDate = getDateUpdated(context, offset);
        Map<String, String> params = new HashMap<>();
        params.put("jql", getJqlQuery(updatedDate));
        params.put("expand", "transitions,changelog");
        params.put("fields", "*all");
        params.put("maxResults", String.valueOf(100));
        return JiraParserUtils.addQueryParams(context.baseUrl() + "/search", params);
    }

    private static String getJqlQuery(String updatedDate) {
        return "updatedDate >= \"" + updatedDate + "\" ORDER BY updatedDate ASC";
    }

    private static String getDateUpdated(JiraOperationContext context, Map<String, ?> offset) {
        return (dateUpdated(offset) != null) ? dateUpdated(offset) :
                DateUtils.getJiraDateAsString(((JiraSourceConnectorConfig)context.config()).since());
    }

    protected JiraResponseParser createParser(String entityName) {
        return (JiraResponseParser)new IssueResponseParser(entityName);
    }

    protected void createNextOperations(Map<String, ?> offsets, List<Operation<JiraOperationContext>> operations) {
        JiraOperationResult result = result();
        ObjectNode response = result.response();
        if (response == null)
            return;
        JsonNode issues = response.get("issues");
        if (issues == null || issues.isEmpty())
            return;
        JsonNode lastIssue = issues.get(issues.size() - 1);
        createDependentOperations(operations, result, issues);
        if (lastIssue != null) {
            setNextPageUrlUsingLastIssue(result, lastIssue);
        } else {
            result.setNextPageUrl(this.urlPath);
        }
        operations.add(new GetIssues(this));
    }

    private void setNextPageUrlUsingLastIssue(JiraOperationResult result, JsonNode lastIssue) {
        String updatedDate = DateUtils.toJiraDateFormat(lastIssue.get("fields").get("updated")
                .asText());
        result.setNextPageUrl(JiraParserUtils.addQueryParam(this.urlPath, "jql", getJqlQuery(updatedDate)));
    }

    private void createDependentOperations(List<Operation<JiraOperationContext>> operations, JiraOperationResult result, JsonNode issues) {
        for (JsonNode issue : issues) {
            Long issueId = Long.valueOf(issue.get("id").asLong());
            IssueMapper issueMapper = new IssueMapper();
            issueMapper.issue((ObjectNode)issue.deepCopy());
            JsonNode changelogs = issue.get("changelog");
            createChangelogOperation(operations, result, issueId, changelogs, issueMapper);
            JsonNode worklogs = issue.get("fields").get("worklog");
            createWorklogOperation(operations, result, issueId, worklogs, issueMapper);
            JsonNode comments = issue.get("fields").get("comment");
            createCommentOperation(operations, result, issueId, comments, issueMapper);
            ((JiraOperationContext)result.context()).issueMapperMap().put(issueId, issueMapper);
        }
    }

    private void createCommentOperation(List<Operation<JiraOperationContext>> operations, JiraOperationResult result, Long issueId, JsonNode comments, IssueMapper issueMapper) {
        List<String> entities = ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).entityNames();
        if (entities.contains(JiraEntity.ISSUE_COMMENTS.name().toLowerCase())) {
            if (hasMoreRecords(comments)) {
                issueMapper.hasComments(true);
                for (JsonNode comment : comments.get("comments"))
                    ((JiraOperationContext)this.context).comments().add(comment.deepCopy());
            } else {
                issueMapper.hasComments(false);
                operations.add(new GetCommentsOperation((JiraOperationContext)this.context, result.offsetFromLastRecord(), issueId));
            }
        } else {
            issueMapper.hasComments(true);
        }
    }

    private void createWorklogOperation(List<Operation<JiraOperationContext>> operations, JiraOperationResult result, Long issueId, JsonNode worklogs, IssueMapper issueMapper) {
        List<String> entities = ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).entityNames();
        if (entities.contains(JiraEntity.WORKLOGS.name().toLowerCase())) {
            if (hasMoreRecords(worklogs)) {
                issueMapper.hasWorklogs(true);
                for (JsonNode worklog : worklogs.get("worklogs"))
                    ((JiraOperationContext)this.context).worklogs().add(worklog.deepCopy());
            } else {
                issueMapper.hasWorklogs(false);
                operations.add(new GetWorklogsOperation((JiraOperationContext)this.context, result.offsetFromLastRecord(), issueId));
            }
        } else {
            issueMapper.hasWorklogs(true);
        }
    }

    private void createChangelogOperation(List<Operation<JiraOperationContext>> operations, JiraOperationResult result, Long issueId, JsonNode changelogs, IssueMapper issueMapper) {
        List<String> entities = ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).entityNames();
        if (entities.contains(JiraEntity.CHANGELOGS.name().toLowerCase())) {
            if (hasMoreRecords(changelogs)) {
                issueMapper.hasChangelogs(true);
                for (JsonNode changelog : changelogs.get("histories"))
                    ((JiraOperationContext)this.context).changelogs().add(changelog.deepCopy());
            } else {
                issueMapper.hasChangelogs(false);
                operations.add(new GetChangelogsOperation((JiraOperationContext)this.context, result.offsetFromLastRecord(), issueId));
            }
        } else {
            issueMapper.hasChangelogs(true);
        }
    }

    private boolean hasMoreRecords(JsonNode node) {
        int totalRecords = node.get("total").asInt();
        int recordsPerPage = node.get("maxResults").asInt();
        return (totalRecords <= recordsPerPage);
    }
}

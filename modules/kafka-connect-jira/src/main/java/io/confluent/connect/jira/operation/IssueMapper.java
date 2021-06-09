package io.confluent.connect.jira.operation;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class IssueMapper {
    private ObjectNode issue;

    private boolean hasComments;

    private boolean hasChangelogs;

    private boolean hasWorklogs;

    public ObjectNode issue() {
        return this.issue;
    }

    public void issue(ObjectNode issue) {
        this.issue = issue;
    }

    public boolean hasComments() {
        return this.hasComments;
    }

    public void hasComments(boolean hasComments) {
        this.hasComments = hasComments;
    }

    public boolean hasWorklogs() {
        return this.hasWorklogs;
    }

    public void hasWorklogs(boolean hasWorklogs) {
        this.hasWorklogs = hasWorklogs;
    }

    public boolean hasChangelogs() {
        return this.hasChangelogs;
    }

    public void hasChangelogs(boolean hasChangelogs) {
        this.hasChangelogs = hasChangelogs;
    }
}

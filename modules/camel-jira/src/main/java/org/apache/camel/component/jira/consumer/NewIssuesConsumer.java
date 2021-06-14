//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.consumer;

import com.atlassian.jira.rest.client.api.domain.Issue;

import java.util.Comparator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jira.JiraEndpoint;

public class NewIssuesConsumer extends AbstractJiraConsumer {
    private final String jql;
    private long latestIssueId = -1L;

    public NewIssuesConsumer(JiraEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.jql = endpoint.getJql() + " ORDER BY key desc";
    }

    protected void doStart() throws Exception {
        super.doStart();
        List<Issue> issues = this.getIssues(this.jql, 0, 50, endpoint.getMaxResults());
        if (!issues.isEmpty()) {
            issues.sort(Comparator.comparing(Issue::getUpdateDate));
            this.latestIssueId = issues.get(issues.size() - 1).getId();
            process(issues);
        }

    }

    protected int poll() throws Exception {
        int nMessages = 0;
        if (this.latestIssueId > -1L) {
            List<Issue> newIssues = this.getNewIssues();
            process(newIssues);
            nMessages = newIssues.size();
        }

        return nMessages;
    }

    private void process(List<Issue> issues) {
        issues.forEach(issue -> {
            try {
                Exchange exchange = this.createExchange(true);
                exchange.getIn().setBody(issue);
                this.getProcessor().process(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Issue> getNewIssues() {
        String jqlFilter = "id > " + this.latestIssueId + " AND " + this.jql;
        List<Issue> issues = this.getIssues(jqlFilter, 0, 50, ((JiraEndpoint) this.getEndpoint()).getMaxResults());
        if (!issues.isEmpty()) {
            this.latestIssueId = (issues.get(0)).getId();
        }

        return issues;
    }
}

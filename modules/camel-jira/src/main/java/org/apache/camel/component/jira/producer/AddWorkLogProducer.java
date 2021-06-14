//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;
import org.joda.time.DateTime;

public class AddWorkLogProducer extends DefaultProducer {
    private static final int DEFAULT_MINUTES_SPENT = -1;

    public AddWorkLogProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named IssueKey");
        } else {
            String comment = (String)exchange.getIn().getBody(String.class);
            if (comment == null) {
                throw new IllegalArgumentException("Missing exchange body, it should specify the string comment.");
            } else {
                int minutesSpent = (Integer)exchange.getIn().getHeader("minutesSpent", -1, Integer.TYPE);
                if (-1 == minutesSpent) {
                    throw new IllegalArgumentException("Missing exchange input header named minutesSpent");
                } else {
                    JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
                    IssueRestClient issueClient = client.getIssueClient();
                    Issue issue = (Issue)issueClient.getIssue(issueKey).claim();
                    WorklogInput worklogInput = WorklogInput.create(issue.getSelf(), comment, new DateTime(), minutesSpent);
                    issueClient.addWorklog(issue.getWorklogUri(), worklogInput);
                }
            }
        }
    }
}

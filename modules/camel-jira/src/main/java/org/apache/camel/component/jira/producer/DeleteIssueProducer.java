//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class DeleteIssueProducer extends DefaultProducer {
    public DeleteIssueProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key to remove it.");
        } else {
            JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
            IssueRestClient issueClient = client.getIssueClient();
            issueClient.deleteIssue(issueKey, true);
        }
    }
}

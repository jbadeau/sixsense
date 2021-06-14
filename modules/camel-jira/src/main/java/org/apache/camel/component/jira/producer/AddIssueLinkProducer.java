//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class AddIssueLinkProducer extends DefaultProducer {
    public AddIssueLinkProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String parentIssueKey = (String)exchange.getIn().getHeader("ParentIssueKey", String.class);
        if (parentIssueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named ParentIssueKey");
        } else {
            String childIssueKey = (String)exchange.getIn().getHeader("ChildIssueKey", String.class);
            if (childIssueKey == null) {
                throw new IllegalArgumentException("Missing exchange input header named ChildIssueKey");
            } else {
                String linkType = (String)exchange.getIn().getHeader("linkType", String.class);
                if (linkType == null) {
                    throw new IllegalArgumentException("Missing exchange input header named linkType");
                } else {
                    String parentIssueComment = (String)exchange.getIn().getBody(String.class);
                    JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
                    IssueRestClient issueClient = client.getIssueClient();
                    Comment comment = null;
                    if (parentIssueComment != null) {
                        comment = Comment.valueOf(parentIssueComment);
                    }

                    LinkIssuesInput linkIssuesInput = new LinkIssuesInput(parentIssueKey, childIssueKey, linkType, comment);
                    issueClient.linkIssue(linkIssuesInput);
                }
            }
        }
    }
}

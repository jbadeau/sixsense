//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import java.net.URI;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class TransitionIssueProducer extends DefaultProducer {
    public TransitionIssueProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        String commentStr = (String)exchange.getIn().getBody(String.class);
        Integer transitionId = (Integer)exchange.getIn().getHeader("IssueTransitionId", Integer.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key to add the comment to.");
        } else if (transitionId == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueTransitionId', it should specify the transition id.");
        } else {
            JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
            IssueRestClient issueClient = client.getIssueClient();
            Issue issue = (Issue)issueClient.getIssue(issueKey).claim();
            TransitionInput transitionInput = new TransitionInput(transitionId);
            issueClient.transition(issue, transitionInput);
            if (commentStr != null) {
                URI commentsUri = issue.getCommentsUri();
                issueClient.addComment(commentsUri, Comment.valueOf(commentStr));
            }

        }
    }
}

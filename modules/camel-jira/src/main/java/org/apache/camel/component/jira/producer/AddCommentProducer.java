//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Visibility;
import java.net.URI;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;
import org.joda.time.DateTime;

public class AddCommentProducer extends DefaultProducer {
    public AddCommentProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        String commentStr = (String)exchange.getIn().getBody(String.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key to add the comment to.");
        } else if (commentStr == null) {
            throw new IllegalArgumentException("Missing exchange body, it should specify the string comment.");
        } else {
            JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
            IssueRestClient issueClient = client.getIssueClient();
            Issue issue = (Issue)issueClient.getIssue(issueKey).claim();
            URI commentsUri = issue.getCommentsUri();
            DateTime now = DateTime.now();
            Comment comment = new Comment(issue.getSelf(), commentStr, (BasicUser)null, (BasicUser)null, now, (DateTime)null, (Visibility)null, (Long)null);
            issueClient.addComment(commentsUri, comment);
        }
    }
}

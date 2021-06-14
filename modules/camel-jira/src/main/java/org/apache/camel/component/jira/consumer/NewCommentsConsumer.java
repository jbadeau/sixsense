//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.consumer;

import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jira.JiraEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewCommentsConsumer extends AbstractJiraConsumer {
    private static final transient Logger LOG = LoggerFactory.getLogger(NewCommentsConsumer.class);
    private Long lastCommentId = -1L;

    public NewCommentsConsumer(JiraEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    protected int poll() throws Exception {
        List<Comment> newComments = this.getComments();
        int max = newComments.size() - 1;

        for(int i = max; i > -1; --i) {
            Comment newComment = (Comment)newComments.get(i);
            Exchange e = this.createExchange(true);
            e.getIn().setBody(newComment);
            this.getProcessor().process(e);
        }

        return newComments.size();
    }

    protected void doStart() throws Exception {
        super.doStart();
        this.getComments();
    }

    private List<Comment> getComments() {
        LOG.debug("Start: Jira NewCommentsConsumer: retrieving issue comments. Last comment id: {}", this.lastCommentId);
        List<Comment> newComments = new ArrayList();
        List<Issue> issues = this.getIssues();
        Iterator var3 = issues.iterator();

        while(var3.hasNext()) {
            Issue issue = (Issue)var3.next();
            Issue fullIssue = (Issue)this.client().getIssueClient().getIssue(issue.getKey()).claim();
            Iterator var6 = fullIssue.getComments().iterator();

            while(var6.hasNext()) {
                Comment comment = (Comment)var6.next();
                if (comment.getId() > this.lastCommentId) {
                    newComments.add(comment);
                }
            }
        }

        var3 = newComments.iterator();

        while(var3.hasNext()) {
            Comment c = (Comment)var3.next();
            if (c.getId() > this.lastCommentId) {
                this.lastCommentId = c.getId();
            }
        }

        LOG.debug("End: Jira NewCommentsConsumer: retrieving issue comments. {} new comments since last run.", newComments.size());
        return newComments;
    }
}

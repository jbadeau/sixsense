//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.util.Iterator;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class WatcherProducer extends DefaultProducer {
    public WatcherProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        List<String> watchersAdd = (List)exchange.getIn().getHeader("IssueWatchersAdd", List.class);
        List<String> watchersRemove = (List)exchange.getIn().getHeader("IssueWatchersRemove", List.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key to add/remove watchers to.");
        } else {
            JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
            boolean hasWatchersToAdd = watchersAdd != null && !watchersAdd.isEmpty();
            boolean hasWatchersToRemove = watchersRemove != null && !watchersRemove.isEmpty();
            if (hasWatchersToAdd || hasWatchersToRemove) {
                IssueRestClient issueClient = client.getIssueClient();
                Issue issue = (Issue)issueClient.getIssue(issueKey).claim();
                Iterator var10;
                String watcher;
                if (hasWatchersToAdd) {
                    var10 = watchersAdd.iterator();

                    while(var10.hasNext()) {
                        watcher = (String)var10.next();
                        issueClient.addWatcher(issue.getWatchers().getSelf(), watcher);
                    }
                }

                if (hasWatchersToRemove) {
                    var10 = watchersRemove.iterator();

                    while(var10.hasNext()) {
                        watcher = (String)var10.next();
                        issueClient.removeWatcher(issue.getWatchers().getSelf(), watcher);
                    }
                }
            }

        }
    }
}

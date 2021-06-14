//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.consumer;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.camel.Processor;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJiraConsumer extends ScheduledPollConsumer {
    private static final transient Logger LOG = LoggerFactory.getLogger(AbstractJiraConsumer.class);
    protected final JiraEndpoint endpoint;

    public AbstractJiraConsumer(JiraEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.setDelay((long)endpoint.getDelay());
    }

    protected List<Issue> getIssues() {
        return this.getIssues(this.endpoint.getJql(), 0, 50, this.endpoint.getMaxResults());
    }

    protected List<Issue> getIssues(String jql, int start, int maxPerQuery, int maxResults) {
        LOG.debug("Start indexing current JIRA issues...");
        ArrayList issues = new ArrayList();

        while(true) {
            SearchRestClient searchRestClient = this.endpoint.getClient().getSearchClient();
            SearchResult searchResult = (SearchResult)searchRestClient.searchJql(jql, maxResults, start, (Set)null).claim();
            Iterator var8 = searchResult.getIssues().iterator();

            while(var8.hasNext()) {
                Issue issue = (Issue)var8.next();
                issues.add(issue);
            }

            if (start >= searchResult.getTotal() || maxResults > 0 && issues.size() >= maxResults) {
                LOG.debug("End indexing current JIRA issues. {} issues indexed.", issues.size());
                return issues;
            }

            start += maxPerQuery;
        }
    }

    protected JiraRestClient client() {
        return this.endpoint.getClient();
    }

    protected abstract int poll() throws Exception;
}

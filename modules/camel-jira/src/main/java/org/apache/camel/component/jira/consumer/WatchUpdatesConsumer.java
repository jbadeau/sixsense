//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.consumer;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jira.JiraEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchUpdatesConsumer extends AbstractJiraConsumer {
    private static final transient Logger LOG = LoggerFactory.getLogger(WatchUpdatesConsumer.class);
    HashMap<Long, Issue> watchedIssues;
    List<String> watchedFieldsList = new ArrayList();
    String watchedIssuesKeys;

    public WatchUpdatesConsumer(JiraEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.watchedFieldsList = Arrays.asList(endpoint.getWatchedFields().split(","));
    }

    protected void doStart() throws Exception {
        super.doStart();
        this.initIssues();
    }

    private void initIssues() {
        this.watchedIssues = new HashMap();
        List<Issue> issues = this.getIssues(((JiraEndpoint)this.getEndpoint()).getJql(), 0, 50, ((JiraEndpoint)this.getEndpoint()).getMaxResults());
        issues.forEach((i) -> {
            Issue var10000 = (Issue)this.watchedIssues.put(i.getId(), i);
        });
        this.watchedIssuesKeys = (String)issues.stream().map(BasicIssue::getKey).collect(Collectors.joining(","));
    }

    protected int poll() throws Exception {
        List<Issue> issues = this.getIssues(((JiraEndpoint)this.getEndpoint()).getJql(), 0, 50, ((JiraEndpoint)this.getEndpoint()).getMaxResults());
        if (this.watchedIssues.values().size() != issues.size()) {
            this.init();
        }

        Iterator var2 = issues.iterator();

        while(var2.hasNext()) {
            Issue issue = (Issue)var2.next();
            this.checkIfIssueChanged(issue);
        }

        return 0;
    }

    private void checkIfIssueChanged(Issue issue) throws Exception {
        Issue original = (Issue)this.watchedIssues.get(issue.getId());
        AtomicBoolean issueChanged = new AtomicBoolean();
        if (original != null) {
            Iterator var4 = this.watchedFieldsList.iterator();

            while(var4.hasNext()) {
                String field = (String)var4.next();
                if (this.hasFieldChanged(issue, original, field)) {
                    issueChanged.set(true);
                }
            }

            if (issueChanged.get()) {
                this.watchedIssues.put(issue.getId(), issue);
            }
        }

    }

    private boolean hasFieldChanged(Issue changed, Issue original, String fieldName) throws Exception {
        Method get = Issue.class.getDeclaredMethod("get" + fieldName);
        Object originalField = get.invoke(original);
        Object changedField = get.invoke(changed);
        if (!Objects.equals(originalField, changedField)) {
            if (!((JiraEndpoint)this.getEndpoint()).isSendOnlyUpdatedField()) {
                this.processExchange(changed, changed.getKey(), fieldName);
            } else {
                this.processExchange(changedField, changed.getKey(), fieldName);
            }

            return true;
        } else {
            return false;
        }
    }

    private void processExchange(Object body, String issueKey, String changed) throws Exception {
        Exchange e = this.createExchange(true);
        e.getIn().setBody(body);
        e.getIn().setHeader("IssueKey", issueKey);
        e.getIn().setHeader("IssueChanged", changed);
        e.getIn().setHeader("IssueWatchedIssues", this.watchedIssuesKeys);
        LOG.debug(" {}: {} changed to {}", new Object[]{issueKey, changed, body});
        this.getProcessor().process(e);
    }
}

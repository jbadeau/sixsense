//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import java.util.Iterator;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class AddIssueProducer extends DefaultProducer {
    public AddIssueProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
        String projectKey = (String)exchange.getIn().getHeader("ProjectKey", String.class);
        Long issueTypeId = (Long)exchange.getIn().getHeader("IssueTypeId", Long.class);
        String issueTypeName = (String)exchange.getIn().getHeader("IssueTypeName", String.class);
        String summary = (String)exchange.getIn().getHeader("IssueSummary", String.class);
        String assigneeName = (String)exchange.getIn().getHeader("IssueAssignee", String.class);
        String priorityName = (String)exchange.getIn().getHeader("IssuePriorityName", String.class);
        Long priorityId = (Long)exchange.getIn().getHeader("IssuePriorityId", Long.class);
        List<String> components = (List)exchange.getIn().getHeader("IssueComponents", List.class);
        List<String> watchers = (List)exchange.getIn().getHeader("IssueWatchersAdd", List.class);
        Iterable priorities;
        Iterator var13;
        if (issueTypeId == null && issueTypeName != null) {
            priorities = (Iterable)client.getMetadataClient().getIssueTypes().claim();
            var13 = priorities.iterator();

            while(var13.hasNext()) {
                IssueType type = (IssueType)var13.next();
                if (issueTypeName.equals(type.getName())) {
                    issueTypeId = type.getId();
                    break;
                }
            }
        }

        if (priorityId == null && priorityName != null) {
            priorities = (Iterable)client.getMetadataClient().getPriorities().claim();
            var13 = priorities.iterator();

            while(var13.hasNext()) {
                Priority pri = (Priority)var13.next();
                if (priorityName.equals(pri.getName())) {
                    priorityId = pri.getId();
                    break;
                }
            }
        }

        if (projectKey == null) {
            throw new IllegalArgumentException("A valid project key is required.");
        } else if (issueTypeId == null) {
            throw new IllegalArgumentException("A valid issue type id is required, actual: id(" + issueTypeId + "), name(" + issueTypeName + ")");
        } else if (summary == null) {
            throw new IllegalArgumentException("A summary field is required, actual value: " + summary);
        } else {
            IssueInputBuilder builder = new IssueInputBuilder(projectKey, issueTypeId);
            builder.setDescription((String)exchange.getIn().getBody(String.class));
            builder.setSummary(summary);
            if (components != null && !components.isEmpty()) {
                builder.setComponentsNames(components);
            }

            if (priorityId != null) {
                builder.setPriorityId(priorityId);
            }

            if (assigneeName != null) {
                builder.setAssigneeName(assigneeName);
            }

            IssueRestClient issueClient = client.getIssueClient();
            BasicIssue issueCreated = (BasicIssue)issueClient.createIssue(builder.build()).claim();
            Issue issue = (Issue)issueClient.getIssue(issueCreated.getKey()).claim();
            if (watchers != null && !watchers.isEmpty()) {
                Iterator var16 = watchers.iterator();

                while(var16.hasNext()) {
                    String watcher = (String)var16.next();
                    issueClient.addWatcher(issue.getWatchers().getSelf(), watcher);
                }
            }

            if (exchange.getPattern().isOutCapable()) {
                exchange.getOut().copyFrom(exchange.getIn());
                exchange.getOut().setBody(issue);
            } else {
                exchange.getIn().setBody(issue);
            }

        }
    }
}

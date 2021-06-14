//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import java.util.Iterator;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;

public class UpdateIssueProducer extends DefaultProducer {
    public UpdateIssueProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) {
        JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key.");
        } else {
            Long issueTypeId = (Long)exchange.getIn().getHeader("IssueTypeId", Long.class);
            String issueTypeName = (String)exchange.getIn().getHeader("IssueTypeName", String.class);
            String summary = (String)exchange.getIn().getHeader("IssueSummary", String.class);
            String assigneeName = (String)exchange.getIn().getHeader("IssueAssignee", String.class);
            String priorityName = (String)exchange.getIn().getHeader("IssuePriorityName", String.class);
            Long priorityId = (Long)exchange.getIn().getHeader("IssuePriorityId", Long.class);
            List<String> components = (List)exchange.getIn().getHeader("IssueComponents", List.class);
            Iterable priorities;
            Iterator var12;
            if (issueTypeId == null && issueTypeName != null) {
                priorities = (Iterable)client.getMetadataClient().getIssueTypes().claim();
                var12 = priorities.iterator();

                while(var12.hasNext()) {
                    IssueType type = (IssueType)var12.next();
                    if (issueTypeName.equals(type.getName())) {
                        issueTypeId = type.getId();
                        break;
                    }
                }
            }

            if (priorityId == null && priorityName != null) {
                priorities = (Iterable)client.getMetadataClient().getPriorities().claim();
                var12 = priorities.iterator();

                while(var12.hasNext()) {
                    Priority pri = (Priority)var12.next();
                    if (priorityName.equals(pri.getName())) {
                        priorityId = pri.getId();
                        break;
                    }
                }
            }

            IssueInputBuilder builder = new IssueInputBuilder();
            if (issueTypeId != null) {
                builder.setIssueTypeId(issueTypeId);
            }

            if (summary != null) {
                builder.setSummary(summary);
            }

            String description = (String)exchange.getIn().getBody(String.class);
            if (description != null) {
                builder.setDescription(description);
            }

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
            issueClient.updateIssue(issueKey, builder.build()).claim();
        }
    }
}

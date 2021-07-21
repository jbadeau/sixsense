/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jira.consumer;

import java.util.Comparator;
import java.util.List;

import com.atlassian.jira.server.rest.client.api.domain.Issue;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jira.JiraEndpoint;

/**
 * Consumes new JIRA issues.
 *
 * NOTE: We manually add "ORDER BY key desc" to the JQL in order to optimize startup (the latest issues one at a time),
 * rather than having to index everything.
 */
public class NewIssuesConsumer extends AbstractJiraConsumer {

    private final String jql;
    private long latestIssueId = -1;

    public NewIssuesConsumer(JiraEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        jql = endpoint.getJql() + " ORDER BY key desc";
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        // read the actual issues, the next poll outputs only the new issues added after the route start
        // grab only the top
        List<Issue> issues = getIssues(jql, 0, 50, endpoint.getMaxResults());
        // in case there aren't any issues...
        if (!issues.isEmpty()) {
            issues.sort(Comparator.comparing(Issue::getUpdateDate));
            this.latestIssueId = issues.get(issues.size() - 1).getId();
            process(issues);
        }
    }

    @Override
    protected int poll() throws Exception {
        // it may happen the poll() is called while the route is doing the initial load,
        // this way we need to wait for the latestIssueId being associated to the last indexed issue id
        int nMessages = 0;
        if (latestIssueId > -1) {
            List<Issue> newIssues = getNewIssues();
            // In the end, we want only *new* issues oldest to newest.
            process(newIssues);
            nMessages = newIssues.size();
        }
        return nMessages;
    }

    private void process(List<Issue> issues) {
        issues.forEach(issue -> {
            try {
                Exchange exchange = this.createExchange(true);
                exchange.getIn().setBody(issue);
                this.getProcessor().process(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<Issue> getNewIssues() {
        // search only for issues created after the latest id
        String jqlFilter = "id > " + latestIssueId + " AND " + jql;
        List<Issue> issues = getIssues(jqlFilter, 0, 50, ((JiraEndpoint) getEndpoint()).getMaxResults());
        if (!issues.isEmpty()) {
            latestIssueId = issues.get(0).getId();
        }
        return issues;
    }
}

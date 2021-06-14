//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.producer;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.io.File;
import java.net.URI;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.component.jira.JiraEndpoint;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachFileProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(AttachFileProducer.class);

    public AttachFileProducer(JiraEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) throws InvalidPayloadException {
        String issueKey = (String)exchange.getIn().getHeader("IssueKey", String.class);
        if (issueKey == null) {
            throw new IllegalArgumentException("Missing exchange input header named 'IssueKey', it should specify the issue key to attach a file.");
        } else {
            File file = (File)exchange.getIn().getMandatoryBody(File.class);
            JiraRestClient client = ((JiraEndpoint)this.getEndpoint()).getClient();
            IssueRestClient issueClient = client.getIssueClient();
            Issue issue = (Issue)issueClient.getIssue(issueKey).claim();
            URI attachmentsUri = issue.getAttachmentsUri();
            issueClient.addAttachments(attachmentsUri, new File[]{file});
        }
    }
}

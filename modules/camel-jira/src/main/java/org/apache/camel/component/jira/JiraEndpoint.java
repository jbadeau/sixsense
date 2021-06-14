//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import java.net.URI;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.jira.consumer.NewCommentsConsumer;
import org.apache.camel.component.jira.consumer.NewIssuesConsumer;
import org.apache.camel.component.jira.consumer.WatchUpdatesConsumer;
import org.apache.camel.component.jira.oauth.JiraOAuthAuthenticationHandler;
import org.apache.camel.component.jira.oauth.OAuthAsynchronousJiraRestClientFactory;
import org.apache.camel.component.jira.producer.AddCommentProducer;
import org.apache.camel.component.jira.producer.AddIssueLinkProducer;
import org.apache.camel.component.jira.producer.AddIssueProducer;
import org.apache.camel.component.jira.producer.AddWorkLogProducer;
import org.apache.camel.component.jira.producer.AttachFileProducer;
import org.apache.camel.component.jira.producer.DeleteIssueProducer;
import org.apache.camel.component.jira.producer.FetchCommentsProducer;
import org.apache.camel.component.jira.producer.FetchIssueProducer;
import org.apache.camel.component.jira.producer.TransitionIssueProducer;
import org.apache.camel.component.jira.producer.UpdateIssueProducer;
import org.apache.camel.component.jira.producer.WatcherProducer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.Registry;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(
        firstVersion = "3.0",
        scheme = "jira",
        title = "Jira",
        syntax = "jira:type",
        category = {Category.API, Category.REPORTING}
)
public class JiraEndpoint extends DefaultEndpoint {
    private static final transient Logger LOG = LoggerFactory.getLogger(JiraEndpoint.class);
    @UriPath
    @Metadata(
            required = true
    )
    private JiraType type;
    @UriParam(
            label = "consumer"
    )
    private String jql;
    @UriParam(
            label = "consumer",
            defaultValue = "Status,Priority"
    )
    private String watchedFields = "Status,Priority";
    @UriParam(
            label = "consumer",
            defaultValue = "true"
    )
    private boolean sendOnlyUpdatedField = true;
    @UriParam(
            label = "consumer",
            defaultValue = "50"
    )
    private Integer maxResults = 50;
    @UriParam
    private JiraConfiguration configuration;
    private transient JiraRestClient client;

    public JiraEndpoint(String uri, JiraComponent component, JiraConfiguration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    public JiraConfiguration getConfiguration() {
        return this.configuration;
    }

    public void doStart() throws Exception {
        super.doStart();
        Registry registry = this.getCamelContext().getRegistry();
        JiraRestClientFactory factory = (JiraRestClientFactory)registry.lookupByNameAndType("JiraRestClientFactory", JiraRestClientFactory.class);
        if (factory == null) {
            factory = (JiraRestClientFactory) new OAuthAsynchronousJiraRestClientFactory();
        }

        URI jiraServerUri = URI.create(this.configuration.getJiraUrl());
        if (this.configuration.getUsername() != null) {
            LOG.info("Jira Basic authentication with username/password.");
            this.client = ((JiraRestClientFactory)factory).createWithBasicHttpAuthentication(jiraServerUri, this.configuration.getUsername(), this.configuration.getPassword());
        } else {
            LOG.info("Jira OAuth authentication.");
            JiraOAuthAuthenticationHandler oAuthHandler = new JiraOAuthAuthenticationHandler(this.configuration.getConsumerKey(), this.configuration.getVerificationCode(), this.configuration.getPrivateKey(), this.configuration.getAccessToken(), this.configuration.getJiraUrl());
            this.client = ((JiraRestClientFactory)factory).create(jiraServerUri, oAuthHandler);
        }

    }

    protected void doStop() throws Exception {
        super.doStop();
        if (this.client != null) {
            this.client.close();
        }

    }

    public Producer createProducer() {
        switch(this.type) {
            case ADDISSUE:
                return new AddIssueProducer(this);
            case ATTACH:
                return new AttachFileProducer(this);
            case ADDCOMMENT:
                return new AddCommentProducer(this);
            case WATCHERS:
                return new WatcherProducer(this);
            case DELETEISSUE:
                return new DeleteIssueProducer(this);
            case UPDATEISSUE:
                return new UpdateIssueProducer(this);
            case TRANSITIONISSUE:
                return new TransitionIssueProducer(this);
            case ADDISSUELINK:
                return new AddIssueLinkProducer(this);
            case ADDWORKLOG:
                return new AddWorkLogProducer(this);
            case FETCHISSUE:
                return new FetchIssueProducer(this);
            case FETCHCOMMENTS:
                return new FetchCommentsProducer(this);
            default:
                throw new IllegalArgumentException("Producer does not support type: " + this.type);
        }
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Object consumer;
        if (this.type == JiraType.NEWCOMMENTS) {
            consumer = new NewCommentsConsumer(this, processor);
        } else if (this.type == JiraType.NEWISSUES) {
            consumer = new NewIssuesConsumer(this, processor);
        } else {
            if (this.type != JiraType.WATCHUPDATES) {
                throw new IllegalArgumentException("Consumer does not support type: " + this.type);
            }

            consumer = new WatchUpdatesConsumer(this, processor);
        }

        this.configureConsumer((Consumer)consumer);
        return (Consumer)consumer;
    }

    public JiraType getType() {
        return this.type;
    }

    public void setType(JiraType type) {
        this.type = type;
    }

    public String getJql() {
        return this.jql;
    }

    public void setJql(String jql) {
        this.jql = jql;
    }

    public int getDelay() {
        return this.configuration.getDelay();
    }

    public JiraRestClient getClient() {
        return this.client;
    }

    public void setClient(JiraRestClient client) {
        this.client = client;
    }

    public Integer getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public String getWatchedFields() {
        return this.watchedFields;
    }

    public void setWatchedFields(String watchChange) {
        this.watchedFields = watchChange;
    }

    public boolean isSendOnlyUpdatedField() {
        return this.sendOnlyUpdatedField;
    }

    public void setSendOnlyUpdatedField(boolean sendOnlyUpdatedField) {
        this.sendOnlyUpdatedField = sendOnlyUpdatedField;
    }
}

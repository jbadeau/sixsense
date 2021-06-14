//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.oauth;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import java.net.URI;

public class OAuthAsynchronousJiraRestClientFactory extends AsynchronousJiraRestClientFactory {
    public OAuthAsynchronousJiraRestClientFactory() {
    }

    public JiraRestClient create(final URI serverUri, final AuthenticationHandler authenticationHandler) {
        DisposableHttpClient httpClient = (new OAuthAsynchronousHttpClientFactory()).createClient(serverUri, authenticationHandler);
        return new AsynchronousJiraRestClient(serverUri, httpClient);
    }
}

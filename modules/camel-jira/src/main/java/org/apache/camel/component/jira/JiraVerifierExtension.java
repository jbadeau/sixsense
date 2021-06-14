//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import java.net.URI;
import java.util.Map;

import org.apache.camel.component.extension.ComponentVerifierExtension.Result.Status;
import org.apache.camel.component.extension.ComponentVerifierExtension.VerificationError.ExceptionAttribute;
import org.apache.camel.component.extension.ComponentVerifierExtension.VerificationError.StandardCode;
import org.apache.camel.component.extension.verifier.DefaultComponentVerifierExtension;
import org.apache.camel.component.extension.verifier.OptionsGroup;
import org.apache.camel.component.extension.verifier.ResultBuilder;
import org.apache.camel.component.extension.verifier.ResultErrorBuilder;
import org.apache.camel.component.extension.verifier.ResultErrorHelper;
import org.apache.camel.component.jira.oauth.JiraOAuthAuthenticationHandler;
import org.apache.camel.component.jira.oauth.OAuthAsynchronousJiraRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraVerifierExtension extends DefaultComponentVerifierExtension {
    private static final Logger LOG = LoggerFactory.getLogger(JiraVerifierExtension.class);

    public JiraVerifierExtension() {
        super("jira");
    }

    protected Result verifyParameters(Map<String, Object> parameters) {
        ResultBuilder builder = ResultBuilder.withStatusAndScope(Status.OK, Scope.PARAMETERS).error(ResultErrorHelper.requiresOption("jiraUrl", parameters)).errors(ResultErrorHelper.requiresAny(parameters, new OptionsGroup[]{OptionsGroup.withName("basic_authentication").options(new String[]{"username", "password", "!requestToken", "!privateKey", "!consumerKey", "!verificationCode", "!accessToken"}), OptionsGroup.withName("oauth_authentication").options(new String[]{"requestToken", "privateKey", "consumerKey", "verificationCode", "accessToken", "!username", "!password"})}));
        super.verifyParametersAgainstCatalog(builder, parameters);
        return builder.build();
    }

    protected Result verifyConnectivity(Map<String, Object> parameters) {
        ResultBuilder builder = ResultBuilder.withStatusAndScope(Status.OK, Scope.CONNECTIVITY);

        ResultErrorBuilder errorBuilder;
        try {
            JiraConfiguration conf = (JiraConfiguration)this.setProperties(new JiraConfiguration(), parameters);
            OAuthAsynchronousJiraRestClientFactory factory = new OAuthAsynchronousJiraRestClientFactory();
            URI jiraServerUri = URI.create(conf.getJiraUrl());
            JiraRestClient client;
            if (conf.getUsername() != null) {
                client = factory.createWithBasicHttpAuthentication(jiraServerUri, conf.getUsername(), conf.getPassword());
            } else {
                JiraOAuthAuthenticationHandler oAuthHandler = new JiraOAuthAuthenticationHandler(conf.getConsumerKey(), conf.getVerificationCode(), conf.getPrivateKey(), conf.getAccessToken(), conf.getJiraUrl());
                client = factory.create(jiraServerUri, oAuthHandler);
            }

            ServerInfo serverInfo = (ServerInfo)client.getMetadataClient().getServerInfo().claim();
            LOG.info("Verify connectivity to jira server OK: {}", serverInfo);
        } catch (RestClientException var8) {
            errorBuilder = ResultErrorBuilder.withCodeAndDescription(StandardCode.AUTHENTICATION, var8.getMessage()).detail("jira_exception_message", var8.getMessage()).detail("jira_status_code", var8.getStatusCode()).detail(ExceptionAttribute.EXCEPTION_CLASS, var8.getClass().getName()).detail(ExceptionAttribute.EXCEPTION_INSTANCE, var8);
            builder.error(errorBuilder.build());
        } catch (Exception var9) {
            errorBuilder = ResultErrorBuilder.withCodeAndDescription(StandardCode.AUTHENTICATION, var9.getMessage()).detail("jira_exception_message", var9.getMessage()).detail(ExceptionAttribute.EXCEPTION_CLASS, var9.getClass().getName()).detail(ExceptionAttribute.EXCEPTION_INSTANCE, var9);
            builder.error(errorBuilder.build());
        }

        return builder.build();
    }
}

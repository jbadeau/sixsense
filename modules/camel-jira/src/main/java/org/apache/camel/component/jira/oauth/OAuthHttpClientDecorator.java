//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.oauth;

import com.atlassian.httpclient.apache.httpcomponents.DefaultRequest.DefaultRequestBuilder;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Request;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.httpclient.api.Request.Builder;
import com.atlassian.httpclient.api.Request.Method;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import java.net.URI;
import java.util.regex.Pattern;

public abstract class OAuthHttpClientDecorator implements DisposableHttpClient {
    private final HttpClient httpClient;
    private final AuthenticationHandler authenticationHandler;
    private URI uri;

    public OAuthHttpClientDecorator(HttpClient httpClient, AuthenticationHandler authenticationHandler) {
        this.httpClient = httpClient;
        this.authenticationHandler = authenticationHandler;
    }

    public void flushCacheByUriPattern(Pattern urlPattern) {
        this.httpClient.flushCacheByUriPattern(urlPattern);
    }

    public Builder newRequest() {
        return new OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder();
    }

    public Builder newRequest(URI uri) {
        Builder builder = new OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder();
        builder.setUri(uri);
        this.uri = uri;
        return builder;
    }

    public Builder newRequest(URI uri, String contentType, String entity) {
        Builder builder = new OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder();
        this.uri = uri;
        builder.setUri(uri);
        builder.setContentType(contentType);
        builder.setEntity(entity);
        return builder;
    }

    public Builder newRequest(String uri) {
        Builder builder = new OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder();
        this.uri = URI.create(uri);
        builder.setUri(this.uri);
        return builder;
    }

    public Builder newRequest(String uri, String contentType, String entity) {
        Builder builder = new OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder();
        this.uri = URI.create(uri);
        builder.setUri(this.uri);
        builder.setContentType(contentType);
        builder.setEntity(entity);
        return builder;
    }

    public <A> com.atlassian.httpclient.api.ResponseTransformation.Builder<A> transformation() {
        return this.httpClient.transformation();
    }

    public ResponsePromise execute(Request request) {
        return this.httpClient.execute(request);
    }

    public class OAuthAuthenticatedRequestBuilder extends DefaultRequestBuilder {
        Method method;

        OAuthAuthenticatedRequestBuilder() {
            super(OAuthHttpClientDecorator.this.httpClient);
        }

        public ResponsePromise execute(Method method) {
            if (OAuthHttpClientDecorator.this.authenticationHandler != null) {
                this.setMethod(method);
                this.method = method;
                OAuthHttpClientDecorator.this.authenticationHandler.configure(this);
            }

            return super.execute(method);
        }

        public URI getUri() {
            return OAuthHttpClientDecorator.this.uri;
        }
    }
}

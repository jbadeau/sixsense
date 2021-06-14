//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.oauth;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClientFactory;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.factory.HttpClientOptions;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthAsynchronousHttpClientFactory {
    private static final String JIRA_REST_CLIENT_VERSION = OAuthAsynchronousHttpClientFactory.MavenUtils.getVersion("com.atlassian.jira", "jira-rest-java-client-api");

    public OAuthAsynchronousHttpClientFactory() {
    }

    public DisposableHttpClient createClient(final URI serverUri, final AuthenticationHandler authenticationHandler) {
        HttpClientOptions options = new HttpClientOptions();
        final DefaultHttpClientFactory defaultHttpClientFactory = new DefaultHttpClientFactory(new OAuthAsynchronousHttpClientFactory.NoOpEventPublisher(), new OAuthAsynchronousHttpClientFactory.RestClientApplicationProperties(serverUri), new ThreadLocalContextManager() {
            public Object getThreadLocalContext() {
                return null;
            }

            public void setThreadLocalContext(Object context) {
            }

            public void clearThreadLocalContext() {
            }
        });
        final HttpClient httpClient = defaultHttpClientFactory.create(options);
        return new OAuthHttpClientDecorator(httpClient, authenticationHandler) {
            public void destroy() throws Exception {
                defaultHttpClientFactory.dispose(httpClient);
            }
        };
    }

    private static final class MavenUtils {
        private static final Logger LOG = LoggerFactory.getLogger(OAuthAsynchronousHttpClientFactory.MavenUtils.class);
        private static final String UNKNOWN_VERSION = "unknown";

        private MavenUtils() {
        }

        static String getVersion(String groupId, String artifactId) {
            Properties props = new Properties();
            String pomProps = String.format("/META-INF/maven/%s/%s/pom.properties", groupId, artifactId);

            try {
                InputStream resourceAsStream = AuthenticationHandler.class.getResourceAsStream(pomProps);
                Throwable var5 = null;

                String var6;
                try {
                    props.load(resourceAsStream);
                    var6 = props.getProperty("version", "unknown");
                } catch (Throwable var16) {
                    var5 = var16;
                    throw var16;
                } finally {
                    if (resourceAsStream != null) {
                        if (var5 != null) {
                            try {
                                resourceAsStream.close();
                            } catch (Throwable var15) {
                                var5.addSuppressed(var15);
                            }
                        } else {
                            resourceAsStream.close();
                        }
                    }

                }

                return var6;
            } catch (Exception var18) {
                LOG.debug("Could not find version for Jira Rest Java Client maven artifact {}:{}. Error: {}", new Object[]{groupId, artifactId, var18.getMessage()});
                return "unknown";
            }
        }
    }

    private static final class RestClientApplicationProperties implements ApplicationProperties {
        private final String baseUrl;

        private RestClientApplicationProperties(URI jiraURI) {
            this.baseUrl = jiraURI.getPath();
        }

        public String getBaseUrl() {
            return this.baseUrl;
        }

        @Nonnull
        public String getBaseUrl(UrlMode urlMode) {
            return this.baseUrl;
        }

        @Nonnull
        public String getDisplayName() {
            return "Atlassian JIRA Rest Java Client";
        }

        @Nonnull
        public String getPlatformId() {
            return "jira";
        }

        @Nonnull
        public String getVersion() {
            return OAuthAsynchronousHttpClientFactory.JIRA_REST_CLIENT_VERSION;
        }

        @Nonnull
        public Date getBuildDate() {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        public String getBuildNumber() {
            return String.valueOf(0);
        }

        public File getHomeDirectory() {
            return new File(".");
        }

        public String getPropertyValue(final String s) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    private static class NoOpEventPublisher implements EventPublisher {
        private NoOpEventPublisher() {
        }

        public void publish(Object o) {
        }

        public void register(Object o) {
        }

        public void unregister(Object o) {
        }

        public void unregisterAll() {
        }
    }
}

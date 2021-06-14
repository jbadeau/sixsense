//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.camel.spi.EndpointUriFactory;
import org.apache.camel.support.component.EndpointUriFactorySupport;

public class JiraEndpointUriFactory extends EndpointUriFactorySupport implements EndpointUriFactory {
    private static final String BASE = ":type";
    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;

    public JiraEndpointUriFactory() {
    }

    public boolean isEnabled(String scheme) {
        return "jira".equals(scheme);
    }

    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + ":type";
        Map<String, Object> copy = new HashMap(properties);
        String uri = this.buildPathParameter(syntax, syntax, "type", (Object)null, true, copy);
        uri = this.buildQueryParameters(uri, copy, encode);
        return uri;
    }

    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    public boolean isLenientProperties() {
        return false;
    }

    static {
        Set<String> props = new HashSet(17);
        props.add("jql");
        props.add("sendOnlyUpdatedField");
        props.add("exchangePattern");
        props.add("type");
        props.add("accessToken");
        props.add("verificationCode");
        props.add("privateKey");
        props.add("lazyStartProducer");
        props.add("password");
        props.add("delay");
        props.add("bridgeErrorHandler");
        props.add("jiraUrl");
        props.add("maxResults");
        props.add("watchedFields");
        props.add("consumerKey");
        props.add("exceptionHandler");
        props.add("username");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        Set<String> secretProps = new HashSet(6);
        secretProps.add("privateKey");
        secretProps.add("password");
        secretProps.add("accessToken");
        secretProps.add("consumerKey");
        secretProps.add("username");
        secretProps.add("verificationCode");
        SECRET_PROPERTY_NAMES = Collections.unmodifiableSet(secretProps);
    }
}

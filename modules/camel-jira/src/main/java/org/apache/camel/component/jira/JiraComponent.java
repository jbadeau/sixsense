//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;

@Component("jira")
public class JiraComponent extends DefaultComponent {
    @Metadata(
            label = "advanced"
    )
    private JiraConfiguration configuration;

    public JiraComponent() {
        this((CamelContext)null);
    }

    public JiraComponent(CamelContext context) {
        super(context);
        this.registerExtension(new JiraVerifierExtension());
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        JiraConfiguration config = this.configuration != null ? this.configuration.copy() : new JiraConfiguration();
        JiraEndpoint endpoint = new JiraEndpoint(uri, this, config);
        endpoint.setType((JiraType)this.getCamelContext().getTypeConverter().convertTo(JiraType.class, remaining));
        this.setProperties(endpoint, parameters);
        return endpoint;
    }

    public JiraConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(JiraConfiguration configuration) {
        this.configuration = configuration;
    }
}

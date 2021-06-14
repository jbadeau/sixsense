//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

@UriParams
public class JiraConfiguration implements Cloneable {
    @UriParam(
            label = "security",
            secret = true
    )
    private String verificationCode;
    @UriParam(
            label = "security",
            secret = true
    )
    private String consumerKey;
    @UriParam(
            label = "security",
            secret = true
    )
    private String privateKey;
    @UriParam(
            label = "security",
            secret = true
    )
    private String accessToken;
    @UriParam
    @Metadata(
            required = true
    )
    private String jiraUrl;
    @UriParam(
            label = "security",
            secret = true
    )
    private String username;
    @UriParam(
            label = "security",
            secret = true
    )
    private String password;
    @UriParam(
            defaultValue = "6000"
    )
    private Integer delay = 6000;

    public JiraConfiguration() {
    }

    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getConsumerKey() {
        return this.consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getJiraUrl() {
        return this.jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public Integer getDelay() {
        return this.delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JiraConfiguration copy() {
        try {
            return (JiraConfiguration)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new RuntimeCamelException(var2);
        }
    }
}

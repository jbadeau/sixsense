//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira.oauth;

import com.atlassian.httpclient.api.Request.Builder;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.apache.ApacheHttpTransport;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.apache.camel.component.jira.oauth.OAuthHttpClientDecorator.OAuthAuthenticatedRequestBuilder;

public class JiraOAuthAuthenticationHandler implements AuthenticationHandler {
    private OAuthParameters parameters;

    public JiraOAuthAuthenticationHandler(String consumerKey, String verificationCode, String privateKey, String accessToken, String jiraUrl) {
        String accessTokenUrl = jiraUrl + "/plugins/servlet/oauth/access-token";
        JiraOAuthGetAccessToken jiraAccessToken = new JiraOAuthGetAccessToken(accessTokenUrl);
        jiraAccessToken.consumerKey = consumerKey;

        try {
            jiraAccessToken.signer = this.getOAuthRsaSigner(privateKey);
        } catch (Exception var9) {
            throw new RestClientException("Error generating the OAuth Authorization RSA Signer", var9);
        }

        jiraAccessToken.transport = new ApacheHttpTransport();
        jiraAccessToken.verifier = verificationCode;
        jiraAccessToken.temporaryToken = accessToken;
        this.parameters = jiraAccessToken.createParameters();
    }

    public void configure(Builder builder) {
        try {
            OAuthAuthenticatedRequestBuilder oauthBuilder = (OAuthAuthenticatedRequestBuilder)builder;
            this.parameters.computeNonce();
            this.parameters.computeTimestamp();
            this.parameters.computeSignature(oauthBuilder.method.name(), new GenericUrl(oauthBuilder.getUri()));
            builder.setHeader("Authorization", this.parameters.getAuthorizationHeader());
        } catch (Exception var3) {
            throw new RestClientException("Error generating the OAuth Authorization request parameter", var3);
        }
    }

    private OAuthRsaSigner getOAuthRsaSigner(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        OAuthRsaSigner oAuthRsaSigner = new OAuthRsaSigner();
        oAuthRsaSigner.privateKey = kf.generatePrivate(keySpec);
        return oAuthRsaSigner;
    }
}

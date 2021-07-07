package io.confluent.connect.jira.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.client.utils.URIBuilder;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraParserUtils {
    private static final Logger log = LoggerFactory.getLogger(JiraParserUtils.class);

    public static URIBuilder getURI(String url) {
        try {
            return new URIBuilder(url);
        } catch (URISyntaxException e) {
            throw new ConnectException("Exception occurred while parsing url", e);
        }
    }

    public static String addQueryParam(String url, String param, String value) {
        URIBuilder uriBuilder = getURI(url);
        return uriBuilder.setParameter(param, value).toString();
    }

    public static String addQueryParams(String url, Map<String, String> params) {
        URIBuilder uri = getURI(url);
        params.forEach(uri::setParameter);
        try {
            uri.build();
        } catch (URISyntaxException uriException) {
            log.error("Error while generating url for Operation {}", uriException);
            throw new ConnectException("Error while generating url for Issue Operation ", uriException);
        }
        return uri.toString();
    }

    public static String getIdFromUrl(String url) {
        String[] splits = url.split("/");
        return splits[splits.length - 2];
    }

    public static JsonNode renameNumberKeyOfJsonNode(JsonNode parentNode) {
        try {
            Iterator<Map.Entry<String, JsonNode>> it = parentNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> field = it.next();
                String key = field.getKey();
                JsonNode value = field.getValue();
                if (value.isObject() && !value.fieldNames().hasNext())
                    ((ObjectNode)parentNode).set(key, null);
                if (value.isArray())
                    for (JsonNode node : value)
                        renameNumberKeyOfJsonNode(node);
                if (Character.isDigit(key.charAt(0))) {
                    ((ObjectNode)parentNode).remove(key);
                    key = "_".concat(key);
                    ((ObjectNode)parentNode).set(key, value);
                    renameNumberKeyOfJsonNode(parentNode);
                    return parentNode;
                }
                renameNumberKeyOfJsonNode(value);
            }
        } catch (Exception exception) {
            throw new ConnectException(
                    String.format("Failed while renaming key for jsonnode: reason=%s", new Object[] { exception.getMessage() }), exception);
        }
        return parentNode;
    }
}

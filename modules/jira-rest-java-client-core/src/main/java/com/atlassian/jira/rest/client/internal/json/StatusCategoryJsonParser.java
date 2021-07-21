package com.atlassian.jira.rest.client.internal.json;

import io.confluent.connect.avro.data.StatusCategory;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;

public class StatusCategoryJsonParser implements JsonObjectParser<StatusCategory> {

    @Override
    public StatusCategory parse(JSONObject json) throws JSONException {
        final URI self = JsonParseUtil.getSelfUri(json);
        final String name = json.getString("name");
        final Long id = JsonParseUtil.getOptionalLong(json, "id");
        final String key = json.getString("key");
        final String colorName = json.getString("colorName");
        return new StatusCategory(id, self.toString(), key, colorName, name);
    }
}

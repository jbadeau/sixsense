/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.server.rest.client.api.domain.Comment;
import com.atlassian.jira.server.rest.client.api.domain.User;
import com.atlassian.jira.server.rest.client.api.domain.Visibility;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;

public class CommentJsonParser implements JsonObjectParser<Comment> {

    public static final String VISIBILITY_KEY = "visibility";
    private final VisibilityJsonParser visibilityJsonParser = new VisibilityJsonParser();

    @Override
    public Comment parse(JSONObject json) throws JSONException {
        final URI selfUri = JsonParseUtil.getSelfUri(json);
        final Long id = JsonParseUtil.getOptionalLong(json, "id");
        final String body = json.getString("body");
        final BasicUser author = JsonParseUtil.parseBasicUser(json.optJSONObject("author"));
        final BasicUser updateAuthor = JsonParseUtil.parseBasicUser(json.optJSONObject("updateAuthor"));

        final Visibility visibility = visibilityJsonParser.parseVisibility(json);

        User authorUser = new User();
        authorUser.setName(author.getName());
        authorUser.setSelf(author.getSelf().toString());
        authorUser.setAccountId(author.getAccountId());
        authorUser.setDisplayName(author.getDisplayName());

        User updateUser = new User();
        updateUser.setName(updateAuthor.getName());
        updateUser.setSelf(updateAuthor.getSelf().toString());
        updateUser.setAccountId(updateAuthor.getAccountId());
        updateUser.setDisplayName(updateAuthor.getDisplayName());

        return new Comment(id, selfUri.toString(), authorUser, updateUser,
                Instant.ofEpochMilli(JsonParseUtil.parseDateTime(json.getString("created")).getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Instant.ofEpochMilli(JsonParseUtil.parseDateTime(json.getString("updated")).getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                body, visibility);
    }
}

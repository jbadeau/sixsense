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
import io.confluent.connect.avro.data.User;
import io.confluent.connect.avro.data.Visibility;
import io.confluent.connect.avro.data.Worklog;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;

public class WorklogJsonParserV5 implements JsonObjectParser<Worklog> {

    private final URI issue;

    public WorklogJsonParserV5(URI issue) {
        this.issue = issue;
    }


    @Override
    public Worklog parse(JSONObject json) throws JSONException {
        final URI self = JsonParseUtil.getSelfUri(json);
        final BasicUser author = JsonParseUtil.parseBasicUser(json.optJSONObject("author"));
        final BasicUser updateAuthor = JsonParseUtil.parseBasicUser(json.optJSONObject("updateAuthor"));
        // comment is optional due to JRJC-49: JIRA can return worklog without comment
        final String comment = json.optString("comment");
        final DateTime creationDate = JsonParseUtil.parseDateTime(json, "created");
        final DateTime updateDate = JsonParseUtil.parseDateTime(json, "updated");
        final DateTime startDate = JsonParseUtil.parseDateTime(json, "started");
        // timeSpentSeconds is not required due to bug: JRADEV-8825 (fixed in 5.0, Iteration 14).
        final int secondsSpent = json.optInt("timeSpentSeconds", 0);
        final Visibility visibility = new VisibilityJsonParser().parseVisibility(json);

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

        return new Worklog(issue.toString(), self.toString(), authorUser, updateUser, comment,
                Instant.ofEpochMilli(creationDate.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Instant.ofEpochMilli(updateDate.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                Instant.ofEpochMilli(startDate.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                secondsSpent / 60, visibility);
    }
}

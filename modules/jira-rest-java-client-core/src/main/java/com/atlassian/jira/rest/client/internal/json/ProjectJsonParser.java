/*
 * Copyright (C) 2010-2012 Atlassian
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

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.BasicProjectRole;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.server.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.server.rest.client.api.domain.IssueType;
import com.atlassian.jira.server.rest.client.api.domain.Version;
import com.google.common.base.Splitter;
import org.apache.commons.collections4.IterableUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectJsonParser implements JsonObjectParser<Project> {

    private final VersionJsonParser versionJsonParser = new VersionJsonParser();
    private final BasicComponentJsonParser componentJsonParser = new BasicComponentJsonParser();
    private final IssueTypeJsonParser issueTypeJsonParser = new IssueTypeJsonParser();
    private final BasicProjectRoleJsonParser basicProjectRoleJsonParser = new BasicProjectRoleJsonParser();

    static Iterable<String> parseExpandos(final JSONObject json) throws JSONException {
        if (json.has("expand")) {
            final String expando = json.getString("expand");
            return Splitter.on(',').split(expando);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Project parse(JSONObject json) throws JSONException {
        URI self = JsonParseUtil.getSelfUri(json);
        final Iterable<String> expandos = parseExpandos(json);
        final BasicUser lead = JsonParseUtil.parseBasicUser(json.getJSONObject("lead"));
        final String key = json.getString("key");
        final Long id = JsonParseUtil.getOptionalLong(json, "id");
        final String name = JsonParseUtil.getOptionalString(json, "name");
        final String urlStr = JsonParseUtil.getOptionalString(json, "url");
        URI uri;
        try {
            uri = urlStr == null || "".equals(urlStr) ? null : new URI(urlStr);
        } catch (URISyntaxException e) {
            uri = null;
        }
        String description = JsonParseUtil.getOptionalString(json, "description");
        if ("".equals(description)) {
            description = null;
        }
        final Collection<Version> versions = JsonParseUtil.parseJsonArray(json.getJSONArray("versions"), versionJsonParser);
        final Collection<BasicComponent> components = JsonParseUtil.parseJsonArray(json
                .getJSONArray("components"), componentJsonParser);
        final JSONArray issueTypesArray = json.optJSONArray("issueTypes");
        final OptionalIterable<IssueType> issueTypes = JsonParseUtil.parseOptionalJsonArray(issueTypesArray, issueTypeJsonParser);
        final Collection<BasicProjectRole> projectRoles = basicProjectRoleJsonParser.parse(JsonParseUtil
                .getOptionalJsonObject(json, "roles"));
        return new Project(expandos, self, key, id, name, description, lead, uri, getVersions(versions), getComponents(components), getIssueTypes(issueTypes), projectRoles);
    }

    private Collection<com.atlassian.jira.rest.client.api.domain.Version> getVersions(Collection<Version> versions) {
        return versions.stream().map(version -> {
            try {
                return new com.atlassian.jira.rest.client.api.domain.Version(new URI(version.getSelf().toString()),
                        version.getId(), version.getName().toString(), version.getDescription().toString(), version.getIsArchived(), version.getIsReleased(), new DateTime(version.getReleaseDate().getNano()));
            } catch (URISyntaxException e) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    private Collection<com.atlassian.jira.rest.client.api.domain.BasicComponent> getComponents(Collection<BasicComponent> components) {
        return components.stream().map(component -> {
                    try {
                        return new com.atlassian.jira.rest.client.api.domain.BasicComponent(new URI(component.getSelf().toString()), component.getId(), component.getName().toString(), component.getDescription().toString());
                    } catch (URISyntaxException e) {
                        return null;
                    }
                }
        ).collect(Collectors.toList());
    }

    private OptionalIterable<com.atlassian.jira.rest.client.api.domain.IssueType> getIssueTypes(OptionalIterable<IssueType> issueTypes) {
        List<com.atlassian.jira.rest.client.api.domain.IssueType> collect = IterableUtils.toList(issueTypes).stream().map(issueType -> {
            try {
                return new com.atlassian.jira.rest.client.api.domain.IssueType(new URI(issueType.getSelf().toString()), issueType.getId(), issueType.getName().toString(), issueType.getIsSubtask(), issueType.getDescription().toString(), new URI(issueType.getIconUri().toString()));
            } catch (URISyntaxException e) {
                return null;
            }
        }).collect(Collectors.toList());
        return new OptionalIterable<>(collect);
    }
}

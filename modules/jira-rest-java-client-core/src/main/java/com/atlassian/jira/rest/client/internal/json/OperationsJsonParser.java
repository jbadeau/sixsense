/*
 * Copyright (C) 2014 Atlassian
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

import io.confluent.connect.avro.data.OperationGroup;
import io.confluent.connect.avro.data.Operations;
import org.apache.commons.collections4.IterableUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collection;

public class OperationsJsonParser implements JsonObjectParser<Operations> {
    private final JsonObjectParser<OperationGroup> groupParser = new OperationGroupJsonParser();

    @Override
    public Operations parse(final JSONObject json) throws JSONException {
        final Collection<OperationGroup> linkGroups = JsonParseUtil.parseJsonArray(json.getJSONArray("linkGroups"), groupParser);
        return new Operations(IterableUtils.toList(linkGroups));
    }
}

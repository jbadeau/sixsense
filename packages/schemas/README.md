# schemas

## install schema

### kafka topic value example

```http request
POST http://apicurio-schema-registry-sixsense-dev.apps-crc.testing:80/apis/ccompat/v6/subjects/JiraIssues-0.1.0-source-jira-issues-value/versions
Content-Type: application/json
Accept: application/vnd.schemaregistry.v1+json, application/vnd.schemaregistry+json, application/json
{
   "schema":"{\"$schema\": \"http://json-schema.org/draft-07/schema#\",\"type\": \"object\",\"properties\": {\"value\": {\"type\": \"string\"},\"key\": {\"type\": \"string\"},\"timestamp\": {\"type\": \"integer\"}},\"required\": [\"value\",\"key\",\"timestamp\"]}",
   "references":[
      {
         "name":"com.sixsense",
         "subject":"childSubject",
         "version":1
      }
   ]
}
```

### delta table example

```http request
POST http://apicurio-schema-registry-sixsense-dev.apps-crc.testing:80/apis/ccompat/v6/subjects/JIRA_ISSUES_SILVER/versions
Content-Type: application/json
Accept: application/vnd.schemaregistry.v1+json, application/vnd.schemaregistry+json, application/json
{
   "schema":"{\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"type\":\"object\",\"properties\":{\"expand\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"self\":{\"type\":\"string\"},\"key\":{\"type\":\"string\"},\"fields\":{\"type\":\"object\",\"properties\":{\"statuscategorychangedate\":{\"type\":\"string\"},\"issuetype\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"iconUrl\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"subtask\":{\"type\":\"boolean\"},\"avatarId\":{\"type\":\"integer\"},\"hierarchyLevel\":{\"type\":\"integer\"}}},\"timespent\":{\"type\":\"integer\"},\"project\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"key\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"projectTypeKey\":{\"type\":\"string\"},\"simplified\":{\"type\":\"boolean\"},\"avatarUrls\":{\"type\":\"object\",\"$ref\":\"#/definitions/AvatarUrls\"}}},\"fixVersions\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"aggregatetimespent\":{\"type\":\"integer\"},\"resolution\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"}}},\"resolutiondate\":{\"type\":\"string\"},\"workratio\":{\"type\":\"integer\"},\"watches\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"watchCount\":{\"type\":\"integer\"},\"isWatching\":{\"type\":\"boolean\"}}},\"lastViewed\":{\"type\":\"string\"},\"created\":{\"type\":\"string\"},\"priority\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"iconUrl\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"}}},\"labels\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"timeestimate\":{\"type\":\"integer\"},\"aggregatetimeoriginalestimate\":{\"type\":\"integer\"},\"versions\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"issuelinks\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"assignee\":{\"type\":\"object\",\"$ref\":\"#/definitions/Author\"},\"updated\":{\"type\":\"string\"},\"status\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"description\":{\"type\":\"string\"},\"iconUrl\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"},\"id\":{\"type\":\"string\"},\"statusCategory\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"id\":{\"type\":\"integer\"},\"key\":{\"type\":\"string\"},\"colorName\":{\"type\":\"string\"},\"name\":{\"type\":\"string\"}}}}},\"timeoriginalestimate\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"timetracking\":{\"type\":\"object\",\"properties\":{\"originalEstimate\":{\"type\":\"string\"},\"remainingEstimate\":{\"type\":\"string\"},\"timeSpent\":{\"type\":\"string\"},\"originalEstimateSeconds\":{\"type\":\"integer\"},\"remainingEstimateSeconds\":{\"type\":\"integer\"},\"timeSpentSeconds\":{\"type\":\"integer\"}}},\"aggregatetimeestimate\":{\"type\":\"integer\"},\"summary\":{\"type\":\"string\"},\"creator\":{\"type\":\"object\",\"$ref\":\"#/definitions/Author\"},\"reporter\":{\"type\":\"object\",\"$ref\":\"#/definitions/Author\"},\"aggregateprogress\":{\"type\":\"object\",\"$ref\":\"#/definitions/Progress\"},\"progress\":{\"type\":\"object\",\"$ref\":\"#/definitions/Progress\"},\"votes\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"votes\":{\"type\":\"integer\"},\"hasVoted\":{\"type\":\"boolean\"}}},\"worklog\":{\"type\":\"object\",\"properties\":{\"startAt\":{\"type\":\"integer\"},\"maxResults\":{\"type\":\"integer\"},\"total\":{\"type\":\"integer\"}}}}}},\"definitions\":{\"AvatarUrls\":{\"type\":\"object\",\"properties\":{\"48x48\":{\"type\":\"string\"},\"24x24\":{\"type\":\"string\"},\"16x16\":{\"type\":\"string\"},\"32x32\":{\"type\":\"string\"}}},\"Author\":{\"type\":\"object\",\"properties\":{\"self\":{\"type\":\"string\"},\"accountId\":{\"type\":\"string\"},\"emailAddress\":{\"type\":\"string\"},\"avatarUrls\":{\"type\":\"object\",\"$ref\":\"#/definitions/AvatarUrls\"},\"displayName\":{\"type\":\"string\"},\"active\":{\"type\":\"boolean\"},\"timeZone\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"}}},\"Progress\":{\"type\":\"object\",\"properties\":{\"progress\":{\"type\":\"integer\"},\"total\":{\"type\":\"integer\"}}}}}",
   "references":[
      {
         "name":"com.sixsense",
         "subject":"childSubject",
         "version":1
      }
   ]
}
```
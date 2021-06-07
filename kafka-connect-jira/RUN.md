docker run -d \
  --name=kafka-connect \
  --net=host \
  -e CONNECT_BOOTSTRAP_SERVERS=localhost:29092 \
  -e CONNECT_GROUP_ID="jira" \
  -e CONNECT_CONFIG_STORAGE_TOPIC="jira-config" \
  -e CONNECT_OFFSET_STORAGE_TOPIC="jira-offsets" \
  -e CONNECT_STATUS_STORAGE_TOPIC="jira-status" \
  -e CONNECT_KEY_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_VALUE_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_INTERNAL_KEY_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_INTERNAL_VALUE_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_REST_ADVERTISED_HOST_NAME="localhost" \
  -e CONNECT_PLUGIN_PATH=/usr/share/java,/usr/share/confluent-hub-components \
  -e CONNECT_CONNECTOR_CLASS=io.confluent.connect.jira.JiraSourceConnector \
  -e CONNECT_JIRA_TABLES=project_categories,projects,issue_comments,roles,changelogs,issue_transitions,resolutions,project_types,issues,users,versions,worklogs \
  -e CONNECT_JIRA_URL=https://six-group.atlassian.net/rest/api/2 \
  -e CONNECT_JIRA_USERNAME=FOO \
  -e CONNECT_JIRA_API_TOKEN=BAR \
  tomassatka/cp-kafka-connect-base:1.0.0

#Local development
-------------------------------------------------------------------
-------------------------------------------------------------------

## Kafka
docu: /home/tomas/Develop/kafka/README.md

### zookeper
cd /home/tomas/Develop/kafka/kafka_2.12-2.8.0
bin/zookeeper-server-start.sh config/zookeeper.properties

### broker
cd /home/tomas/Develop/kafka/kafka_2.12-2.8.0
bin/kafka-server-start.sh config/server.properties
-------------------------------------------------------------------


## Schema registry
docker run -i -e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=localhost:9092 -e SCHEMA_REGISTRY_HOST_NAME=localhost --network="host" confluentinc/cp-schema-registry
-------------------------------------------------------------------


## Jira Source connector
docu: /home/tomas/Develop/sixsense/kafka-connect-jira/local/README.md

cd /home/tomas/Develop/sixsense/kafka-connect-jira/local
docker run -i \
  --name=kafka-connect \
  --net=host \
  -e CONNECT_BOOTSTRAP_SERVERS=localhost:9092 \
  -e CONNECT_GROUP_ID="jira" \
  -e CONNECT_CONFIG_STORAGE_TOPIC="jira-config" \
  -e CONNECT_OFFSET_STORAGE_TOPIC="jira-offsets" \
  -e CONNECT_STATUS_STORAGE_TOPIC="jira-status" \
  -e CONNECT_KEY_CONVERTER="io.confluent.connect.avro.AvroConverter" \
  -e CONNECT_VALUE_CONVERTER="io.confluent.connect.avro.AvroConverter" \
  -e CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL="http://localhost:8081" \
  -e CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL="http://localhost:8081" \
  -e CONNECT_REST_ADVERTISED_HOST_NAME="localhost" \
  -e CONNECT_PLUGIN_PATH=/usr/share/java,/usr/share/confluent-hub-components \
  -e CONNECT_CONNECTOR_CLASS=io.confluent.connect.jira.JiraSourceConnector \
  -e CONNECT_JIRA_TABLES=project_categories,projects,issue_comments,changelogs,issue_transitions,resolutions,project_types,issues,users,versions,worklogs \
  -e CONNECT_JIRA_URL=https://six-group.atlassian.net \
  -e CONNECT_JIRA_USERNAME=satka.tomas@gmail.com \
  -e CONNECT_JIRA_API_TOKEN=gMjFBXO1vS6iv1lNVekPB610 \
  -e CONNECTOR_CONFIG_PROPERTIES=/config/config.properties \
  -e CONNECT_OFFSET_STORAGE_FILE_FILENAME=/tmp/connect.offsets \
  -v /home/tomas/Develop/sixsense/kafka-connect-jira/local/config:/config \
  tomassatka/cp-kafka-connect-base:1.0.3
-------------------------------------------------------------------


## Minio
cd /home/tomas/Develop/sixsense/minio/local

docker run -p 9000:9000 \
  --name minio \
  -v /home/tomas/Develop/sixsense/minio/local/data:/data \
  -e "MINIO_ROOT_USER=admin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data


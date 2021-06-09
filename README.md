# Local development

-------------------------------------------------------------------
## Kafka
documentation: ../kafka/README.md

### Zookeper
`cd ../kafka/kafka_2.12-2.8.0`  
`bin/zookeeper-server-start.sh config/zookeeper.properties`

### Broker
`cd ../kafka/kafka_2.12-2.8.0`  
`bin/kafka-server-start.sh config/server.properties`

-------------------------------------------------------------------

## Schema registry
```shell
docker run -i \
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=localhost:9092 \
-e SCHEMA_REGISTRY_HOST_NAME=localhost \
--network="host" \
confluentinc/cp-schema-registry
```

-------------------------------------------------------------------
## Jira Source connector
documentation: ../kafka-connect-jira/local/README.md

`cd ../sixsense/kafka-connect-jira/local`

```shell
  docker run -i \
  --name=kafka-connect \
  --net=host \
  -p 5005:5005/tcp \
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
  -e CONNECT_JIRA_TABLES=issues \
  -e CONNECT_JIRA_URL=https://six-group.atlassian.net \
  -e CONNECT_JIRA_USERNAME=jose.badeau@gmail.com \
  -e CONNECT_JIRA_API_TOKEN=WsRuJw26jXdWEtlxKBhI41A0 \
  -e CONNECTOR_CONFIG_PROPERTIES=/config/config.properties \
  -e CONNECT_OFFSET_STORAGE_FILE_FILENAME=/tmp/connect.offsets \
  -e CONNECT_LOG4J_ROOT_LOGLEVEL=DEBUG \
  -e KAFKA_DEBUG=y \
  -e DEBUG_SUSPEND_FLAG=y \
  -v /home/tomas/Develop/sixsense/kafka-connect-jira/local/config:/config \
  tomassatka/cp-kafka-connect-base:1.0.12
```


-------------------------------------------------------------------
## Minio
`cd ../minio/local`

```shell
docker run -p 9000:9000 \
  --name minio \
  -v "$(pwd)"/data:/data \
  -e "MINIO_ROOT_USER=admin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data
```

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
## Camel Jira Source connector
[Oauth steps for atlassian](https://developer.atlassian.com/server/jira/platform/oauth/)  
[Documentation for jira connector](https://camel.apache.org/camel-kafka-connector/latest/reference/connectors/camel-jira-kafka-source-connector.html)

`cd ../sixsense/kafka-connect-jira/local`

* logs:
  * CONNECT_LOG4J_ROOT_LOGLEVEL
* debug: 
  * KAFKA_DEBUG
  * DEBUG_SUSPEND_FLAG


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
  -e CONNECTOR_CONFIG_PROPERTIES=/config/config.properties \
  -e CONNECT_OFFSET_STORAGE_FILE_FILENAME=/tmp/connect.offsets \
  -e CONNECT_LOG4J_ROOT_LOGLEVEL=DEBUG \
  -e KAFKA_DEBUG=y \
  -e DEBUG_SUSPEND_FLAG=y \
  -v "$(pwd)"/config:/config \
  tomassatka/camel-jira-kafka-connector:0.0.7
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

#kafka-connect jira 
1/ take base image: https://hub.docker.com/layers/confluentinc/cp-kafka-connect-base/6.1.1
2/ extend image to create customized jira kafka-connect image: https://docs.confluent.io/platform/current/installation/docker/development.html#extending-images
   jira source connector plugin page: https://docs.confluent.io/kafka-connect-jira/current/configuration_options.html#jira-connector-config
3/ running connect image in docker: https://docs.confluent.io/platform/current/installation/docker/config-reference.html#kconnect-long-configuration
4/ make basic helm chart by `helm create kafka-connect .`
5/ deploy by `helm install kafka-connect .`

6/ start connector over api:

curl -i -X PUT -H "Content-Type:application/json" http://localhost:8083/connectors/jira-connector/config \
-d '{\
"connector.class": "io.confluent.connect.jira.JiraSourceConnector", \
"jira.tables": "project_categories", \
"jira.username": "foo", \
"jira.api.token": "bar", \
"jira.url": "https://six-group.atlassian.net/rest/api/2", \
"confluent.topic.bootstrap.servers": "kafka-cluster-kafka-bootstrap:9092", \
"errors.log.enable": "true", \
"errors.log.include.messages": "true"\
}'

## api described: 
 - https://docs.confluent.io/platform/current/connect/references/restapi.html

## guidence to read for error handling:
 - https://www.confluent.io/blog/kafka-connect-deep-dive-error-handling-dead-letter-queues/



/usr/bin/connect-standalone [-daemon] connect-standalone.properties

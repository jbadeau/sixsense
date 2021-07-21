FROM quay.io/strimzi/kafka:0.23.0-kafka-2.8.0

USER root:root

##########
# Connector camel-jira-kafka-connector
##########
ADD target/camel-jira-kafka-connector-*-SNAPSHOT-package.tar.gz /opt/kafka/plugins/camel-jira-kafka-connector

USER nobody

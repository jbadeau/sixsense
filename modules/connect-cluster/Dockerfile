FROM quay.io/strimzi/kafka:0.23.0-kafka-2.8.0

USER root:root

##########
# Connector camel-jira-kafka-connector
##########
RUN mkdir -p /opt/kafka/plugins/camel-jira-kafka-connector \
      && curl -L --output /opt/kafka/plugins/camel-jira-kafka-connector/camel-jira-kafka-connector-0.11.0-20210623.191157-6-package.tar.gz https://panel.repsy.io/mvn/jbadeau/default//org/apache/camel/kafkaconnector/camel-jira-kafka-connector/0.11.0-SNAPSHOT/camel-jira-kafka-connector-0.11.0-20210623.191157-6-package.tar.gz \
      && tar xvfz /opt/kafka/plugins/camel-jira-kafka-connector/camel-jira-kafka-connector-0.11.0-20210623.191157-6-package.tar.gz -C /opt/kafka/plugins/camel-jira-kafka-connector \
      && rm -vf /opt/kafka/plugins/camel-jira-kafka-connector/camel-jira-kafka-connector-0.11.0-20210623.191157-6-package.tar.gz

USER nobody

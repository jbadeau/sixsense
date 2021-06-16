FROM confluentinc/cp-kafka-connect-base:6.1.1
ADD target/camel-jira-kafka-connector-0.9.0-package.tar.gz /usr/share/confluent-hub-components
COPY launch /etc/confluent/docker/launch
USER root
RUN chmod 777 /home/appuser /opt &&  \
    chmod a+x /etc/confluent/docker/launch && \
    mkdir -p /opt/kafka && \
    ln -s /etc/confluent/docker/run /opt/kafka/kafka_connect_run.sh
USER nobody

FROM confluentinc/cp-kafka-connect-base:6.1.1

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jira:1.0.4-preview
COPY launch /etc/confluent/docker/launch
USER root
RUN chmod 777 /home/appuser /opt
RUN mkdir -p /opt/kafka && ln -s /etc/confluent/docker/run /opt/kafka/kafka_connect_run.sh
USER nobody

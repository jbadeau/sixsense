FROM quay.io/strimzi/kafka:0.23.0-kafka-2.8.0

USER root:root

ADD castorm-kafka-connect-http-0.8.11.zip .
RUN unzip ./castorm-kafka-connect-http-0.8.11.zip -d /opt/kafka/plugins/ && \
    rm ./castorm-kafka-connect-http-0.8.11.zip

USER nobody
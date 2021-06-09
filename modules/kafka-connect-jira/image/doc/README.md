# Introduction

This project provides connector for Kafka Connect to read data from Jira.

# Documentation

Documentation on the connector is hosted on Confluent's
[docs site](https://docs.confluent.io/current/connect/kafka-connect-jira/).

Source code is located in Confluent's
[docs repo](https://github.com/confluentinc/docs/tree/master/connect/kafka-connect-jira). If changes
are made to configuration options for the connector, be sure to generate the RST docs (as described
below) and open a PR against the docs repo to publish those changes!

# Configs

Documentation on the configurations for each connector can be automatically generated via Maven.

To generate documentation for the source connector:
```bash
mvn -Pdocs exec:java@source-config-docs
```

# Compatibility:

This connector has been tested against the Confluent Platform and supports compatibility with
 versions 5.3.x.


package io.confluent.connect.jira.operation;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JiraSourceRecord {
    private String type;

    private ObjectNode data;

    public String getType() {
        return this.type;
    }

    public ObjectNode getData() {
        return this.data;
    }

    public JiraSourceRecord type(String type) {
        this.type = type;
        return this;
    }

    public JiraSourceRecord data(ObjectNode data) {
        this.data = data;
        return this;
    }
}


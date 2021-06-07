package com.sixgroup.sixsense.kafka.connect.jira.operation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.connect.operations.OperationContext;
import io.confluent.connect.operations.OperationResult;
import io.confluent.connect.operations.OperationStatus;
import java.util.Map;

public class JiraOperationResult extends OperationResult<JiraOperationContext> implements OperationStatus {
    private ObjectNode response;

    private int responseSize;

    public JiraOperationResult(JiraOperationContext context, Map<String, ?> initialOffset, OperationResult.RecordFilter recordFilter) {
        super(context, initialOffset, recordFilter);
    }

    public void response(ObjectNode response) {
        this.response = response;
    }

    public ObjectNode response() {
        return this.response;
    }

    public void responseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public int responseSize() {
        return this.responseSize;
    }
}

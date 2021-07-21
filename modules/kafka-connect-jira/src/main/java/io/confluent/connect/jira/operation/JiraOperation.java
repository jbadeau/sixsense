package io.confluent.connect.jira.operation;

import io.confluent.connect.jira.parser.JiraResponseParser;
import io.confluent.connect.operations.OperationStatus;
import io.confluent.connect.operations.http.operation.HttpOperation;
import io.confluent.connect.operations.http.operation.HttpResponseParser;
import java.util.Map;

public class JiraOperation extends HttpOperation<JiraOperationContext> {
    public static final String OFFSET_DATE_UPDATED = "date_updated";

    public static final int MAX_RESULT_SIZE = 100;

    public JiraOperation(JiraOperationContext context, String entityName, String urlPath, Map<String, ?> offset) {
        super(context, entityName, urlPath, offset);
        registerResponseParsers(new HttpResponseParser[] { (HttpResponseParser)createParser(entityName) });
    }

    protected static String dateUpdated(Map<String, ?> offset) {
        if (offset != null)
            return (String)offset.get("date_updated");
        return null;
    }

    protected void resetForRetry() {
        OperationStatus status = status();
        if (status.isRetriable())
            setDelay(status.retryDelay());
        createResult((JiraOperationContext)this.context);
    }

    public JiraOperationResult result() {
        return (JiraOperationResult)super.result();
    }

    protected JiraOperationResult createResult(JiraOperationContext context) {
        return new JiraOperationResult(context, originalOffset(), null);
    }

    protected JiraResponseParser createParser(String entityName) {
        return new JiraResponseParser(entityName);
    }

    public String urlPath() {
        return this.urlPath;
    }
}

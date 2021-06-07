package com.sixgroup.sixsense.kafka.connect.jira.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sixgroup.sixsense.kafka.connect.jira.JiraEntity;
import com.sixgroup.sixsense.kafka.connect.jira.JiraSourceConnectorConfig;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationContext;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraOperationResult;
import com.sixgroup.sixsense.kafka.connect.jira.operation.JiraSourceRecord;
import com.sixgroup.sixsense.kafka.connect.jira.utils.DateUtils;
import io.confluent.connect.operations.OperationResult;
import io.confluent.connect.operations.http.json.JsonParser;
import io.confluent.connect.operations.http.json.JsonSchemaDetector;
import io.confluent.connect.operations.http.operation.HttpResponseParser;
import io.confluent.connect.utils.schema.SchemaNameFormatters;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class JiraResponseParser implements HttpResponseParser<JiraOperationContext> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    protected final ObjectMapper mapper;

    private final JsonParser parser = new JsonParser();

    private final JsonSchemaDetector schemaDetector;

    protected final String entity;

    public JiraResponseParser(String entityFieldName) {
        this.entity = entityFieldName;
        this.schemaDetector = createSchemaDetector();
        this.mapper = createObjectMapper();
    }

    public List<ContentType> supportedContentTypes() {
        return Collections.singletonList(ContentType.APPLICATION_JSON);
    }

    protected JsonSchemaDetector createSchemaDetector() {
        return (
                (JsonSchemaDetector.Builder)JsonSchemaDetector.create()
                        .withNameFormatter(SchemaNameFormatters.avroCompatibleFormatter()))
                .build();
    }

    protected ObjectMapper createObjectMapper() {
        return MAPPER;
    }

    public void parseResponse(HttpRequestBase request, HttpEntity responseEntity, OperationResult<JiraOperationContext> result) throws IOException, InterruptedException {
        JiraOperationResult jiraOperationResult = (JiraOperationResult)result;
        List<ObjectNode> results = (List<ObjectNode>)this.mapper.readValue(responseEntity
                .getContent(), new TypeReference<List<ObjectNode>>() {

        });
        jiraOperationResult.responseSize(results.size());
        for (ObjectNode node : results)
            parseEntity(jiraOperationResult, node, this.entity);
    }

    public void parseEntity(JiraOperationResult result, ObjectNode node, String entity) throws IOException, InterruptedException {
        JiraSourceRecord record = (new JiraSourceRecord()).type(entity).data(node);
        parseObject(record, result, entity);
    }

    public void parseObject(JiraSourceRecord record, JiraOperationResult result, String entity) throws IOException, InterruptedException {
        JsonNode node = this.mapper.valueToTree(record);
        if (node != null)
            parseJsonNode(node, result, entity);
    }

    private void parseJsonNode(JsonNode node, JiraOperationResult result, String entity) throws IOException, InterruptedException {
        assert node != null;
        SchemaAndValue schemaAndValue = this.parser.parse(node, this.schemaDetector);
        Object value = schemaAndValue.value();
        Schema valueSchema = schemaAndValue.schema();
        Map<String, ?> offset = offsetsForValue(node, entity);
        Schema keySchema = keySchema(value);
        SourceRecord record = new SourceRecord(((JiraOperationContext)result.context()).getPartitionByEntityName(entity), offset, ((JiraSourceConnectorConfig)((JiraOperationContext)result.context()).config()).topicName(entity), keySchema, key(value, keySchema), valueSchema, value);
        result.enqueueRecord(record);
    }

    private Schema keySchema(Object value) {
        SchemaBuilder builder = SchemaBuilder.struct();
        Struct valueStruct = (Struct)value;
        Struct data = (Struct)valueStruct.get("data");
        if (data.schema().field("id") != null)
            builder.field("id", (Schema)SchemaBuilder.string());
        return builder.build();
    }

    private Object key(Object value, Schema schema) {
        Struct struct = new Struct(schema);
        Struct valueStruct = (Struct)value;
        Struct data = (Struct)valueStruct.get("data");
        if (data.schema().field("id") != null)
            struct.put(schema.field("id"), this.entity + "_" + data.get("id"));
        return struct;
    }

    private Map<String, ?> offsetsForValue(JsonNode node, String entity) {
        if (entity.equals(JiraEntity.ISSUES.name().toLowerCase())) {
            String updatedDate = DateUtils.toJiraDateFormat(node.at("/data/fields/updated").asText());
            return offsetMap(updatedDate);
        }
        return offsetMap(null);
    }

    private Map<String, String> offsetMap(String updatedTime) {
        Map<String, String> offset = new HashMap<>();
        offset.put("date_updated", updatedTime);
        return offset;
    }
}
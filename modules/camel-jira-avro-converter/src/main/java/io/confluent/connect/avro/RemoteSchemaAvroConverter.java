package io.confluent.connect.avro;

import com.atlassian.jira.rest.client.api.domain.simple.SimplifiedIssue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.avro.SchemaParseException;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.storage.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class RemoteSchemaAvroConverter implements Converter {

    private static final Logger logger = LoggerFactory.getLogger(RemoteSchemaAvroConverter.class);

    /**
     * The default schema cache size. We pick 50 so that there's room in the cache for some recurring
     * nested types in a complex schema.
     */
    private Integer schemaCacheSize = 50;

    private org.apache.avro.Schema parsedSchema = null;
    private AvroSchema avroSchema = null;
    private Schema connectSchema = null;
    private AvroData avroDataHelper = null;
    private boolean isKey;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.isKey = isKey;
        if (configs.get("schema.cache.size") instanceof Integer) {
            schemaCacheSize = (Integer) configs.get("schema.cache.size");
        }

        avroDataHelper = new AvroData(schemaCacheSize);

        if (configs.get("registry.url") instanceof String) {
            String avroSchemaPath = (String) configs.get("registry.url");
            org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
            try {
                URL url = new URL(avroSchemaPath);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                try (
                        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
                        BufferedReader bufferReader = new BufferedReader(inputStreamReader)) {
                    String inputLine;
                    StringBuilder remoteAvroSchema = new StringBuilder();
                    while ((inputLine = bufferReader.readLine()) != null) {
                        remoteAvroSchema.append(inputLine);
                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(remoteAvroSchema.toString());

                    parsedSchema = parser.parse(jsonNode.get("schema").asText());
                    avroSchema = new AvroSchema(parsedSchema);
                    connectSchema = avroDataHelper.toConnectSchema(parsedSchema);

                    mapper.registerModule(new JodaModule());
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                } catch (ProtocolException e) {
                    throw new IllegalStateException("Unable to parse Avro schema when starting RegistrylessAvroConverter due to protocol exception", e);
                } catch (SchemaParseException | IOException spe) {
                    throw new IllegalStateException("Unable to parse Avro schema when starting RegistrylessAvroConverter", spe);
                }
                con.disconnect();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse Avro schema when starting RegistrylessAvroConverter due to malformed url in registry.url", e);
            }
        }
    }

    @Override
    public byte[] fromConnectData(String topic, Schema schema, Object value) {
        if (value == null) {
            return null;
        }
        try {
            String jsonRaw = mapper.writeValueAsString(value);
            SimplifiedIssue issue = mapper.readValue(jsonRaw, SimplifiedIssue.class);
            String json = getJsonString(issue);

            try (
                    InputStream inputStream = new ByteArrayInputStream(json.getBytes());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
            {
                DatumReader<GenericRecord> reader = new GenericDatumReader<>(parsedSchema);
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                DataFileWriter<GenericRecord> writer = new DataFileWriter<>(new GenericDatumWriter<>());
                writer.create(parsedSchema, byteArrayOutputStream);
                Decoder decoder = DecoderFactory.get().jsonDecoder(parsedSchema, dataInputStream);
                GenericRecord datum;
                while (true) {
                    try {
                        datum = reader.read(null, decoder);
                    } catch (EOFException eofe) {
                        break;
                    }
                    writer.append(datum);
                }
                writer.flush();
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new DataException("Error serializing Avro", e);
        }
    }

    public static String getJsonString(GenericContainer record) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            JsonEncoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), byteArrayOutputStream);
            DatumWriter<GenericContainer> writer = new GenericDatumWriter<>();
            if (record instanceof SpecificRecord) {
                writer = new SpecificDatumWriter<>();
            }
            writer.setSchema(record.getSchema());
            writer.write(record, encoder);
            encoder.flush();
            return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    @Override
    public SchemaAndValue toConnectData(String topic, byte[] value) {
        DatumReader<GenericRecord> datumReader;
        if (parsedSchema != null) {
            datumReader = new GenericDatumReader<>(parsedSchema);
        } else {
            datumReader = new GenericDatumReader<>();
        }
        GenericRecord instance = null;

        try (
                SeekableByteArrayInput sbai = new SeekableByteArrayInput(value);
                DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(sbai, datumReader);
        ) {
            instance = dataFileReader.next(instance);
            if (instance == null) {
                logger.warn("Instance was null");
            }

            if (parsedSchema != null) {
                return avroDataHelper.toConnectData(parsedSchema, instance);
            } else {
                return avroDataHelper.toConnectData(instance.getSchema(), instance);
            }
        } catch (IOException ioe) {
            throw new DataException("Failed to deserialize Avro data from topic %s :".format(topic), ioe);
        }
    }
}

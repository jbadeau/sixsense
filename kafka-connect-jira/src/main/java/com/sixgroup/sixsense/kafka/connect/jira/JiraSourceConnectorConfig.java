package com.sixgroup.sixsense.kafka.connect.jira;

import com.sixgroup.sixsense.kafka.connect.jira.utils.DateUtils;
import com.sixgroup.sixsense.kafka.connect.jira.validator.Validator;
import io.confluent.connect.operations.OperationConfig;
import io.confluent.connect.operations.http.operation.AuthType;
import io.confluent.connect.operations.http.operation.HttpOperationConfig;
import io.confluent.connect.operations.rest.RestServiceSourceConnectorConfig;
import io.confluent.connect.utils.ConfigKeys;
import io.confluent.connect.utils.recommenders.Recommenders;
import io.confluent.connect.utils.validators.Validators;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.config.ConfigDef;

public class JiraSourceConnectorConfig extends RestServiceSourceConnectorConfig {
    private static final String JIRA_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private static final String TOPIC_NAME_PATTERN_REGEX = ".*[$][{]entityName[}].*";

    public static final String SINCE_CONFIG = "jira.since";

    private static final String SINCE_CONFIG_DOC = " Issues updated after this time will be processed by the connector. If left blank, the default time will be set to the time this connector is launched. The expected format for jira.since is yyyy-MM-dd HH:mm.";

    private static final String SINCE_CONFIG_DISPLAY = "Since";

    public static final String JIRA_TABLES_CONFIG = "jira.tables";

    private static final String JIRA_TABLES_CONFIG_DOC = "The tables that are to be extracted and written to Kafka.";

    private static final String JIRA_TABLES_CONFIG_DISPLAY = "Jira Tables";

    public static final String JIRA_USERNAME_CONFIG = "jira.username";

    private static final String JIRA_USERNAME_CONFIG_DOC = "The username to be used to authenticate with Jira.";

    private static final String JIRA_USERNAME_CONFIG_DISPLAY = "Jira Username";

    public static final String JIRA_TOKEN_CONFIG = "jira.api.token";

    private static final String JIRA_TOKEN_CONFIG_DOC = "The password to be used to authenticate with Jira";

    private static final String JIRA_TOKEN_CONFIG_DISPLAY = "Jira Token";

    public static final String JIRA_URL_CONFIG = "jira.url";

    private static final String JIRA_URL_CONFIG_DOC = "Jira Service Url.";

    private static final String JIRA_URL_CONFIG_DISPLAY = "Jira Endpoint";

    private static final long REQUEST_INTERVAL_MS_DEFAULT = TimeUnit.DAYS.toMillis(1L);

    private static final String REQUEST_INTERVAL_MS_DISPLAY = "Request Interval (ms)";

    private static final String JIRA_REST_API_VERSION = "/rest/api/2";

    private static final String JIRA_GROUP = "Jira";

    private static final String AUTHORIZATION_GROUP = "Authorization";

    private final Date since;

    public JiraSourceConnectorConfig(Map<?, ?> originals) {
        this(config().toConfigDef(), originals);
    }

    public JiraSourceConnectorConfig(ConfigDef configDef, Map<?, ?> originals) {
        super(configDef, mapProperties((Map)originals));
        this.since = DateUtils.getJiraDate(getString("jira.since"));
        Validator.validateTables(getList("jira.tables"));
        Validator.validateTokenConfig(getPassword("jira.api.token").value());
    }

    public static ConfigKeys config() {
        ConfigKeys keys = new ConfigKeys();
        addJiraConfig(keys);
        OperationConfig.addLimits(keys, "Limits");
        addTopicNamePattern(keys, "Topics");
        keys.get("request.interval.ms")
                .validator((ConfigDef.Validator)Validators.between(Integer.valueOf(1), Long.valueOf(REQUEST_INTERVAL_MS_DEFAULT)))
                .displayName("Request Interval (ms)")
                .defaultValue(Long.valueOf(REQUEST_INTERVAL_MS_DEFAULT));
        keys.get("topic.name.pattern")
                .validator((ConfigDef.Validator)Validators.pattern(".*[$][{]entityName[}].*"));
        OperationConfig.addRetries(keys, "Retries");
        HttpOperationConfig.addProxy(keys, "Proxy");
        keys.alter(key -> key.importance(ConfigDef.Importance.LOW), key -> key.name().contains("http.proxy."));
        addLicense(keys, "License");
        HttpOperationConfig.addSslSupport(keys, "SSL");
        keys.alter(key -> key.internal(true), key -> key.name().contains("ssl."));
        keys.get("https.ssl.enabled").recommender(Recommenders.visibleIf("https.ssl.enabled", value -> false));
        keys.get("connection.user").recommender(Recommenders.visibleIf("connection.user", value -> false));
        keys.get("connection.password").recommender(Recommenders.visibleIf("connection.password", value -> false));
        return keys;
    }

    protected static Map<?, ?> mapProperties(Map<String, String> originals) {
        originals.put("entity.names", originals.get("jira.tables"));
        originals.put("connection.user", originals.get("jira.username"));
        originals.put("connection.password", originals.get("jira.api.token"));
        originals.put("url", originals.get("jira.url"));
        return originals;
    }

    public static void main(String[] args) {
        System.out.println(config().toConfigDef().toEnrichedRst());
    }

    public static void addJiraConfig(ConfigKeys configKeys) {
        configKeys
                .define("jira.since", ConfigDef.Type.STRING)
                .defaultValue(DateUtils.getJiraDateAsString(new Date()))
                .documentation(" Issues updated after this time will be processed by the connector. If left blank, the default time will be set to the time this connector is launched. The expected format for jira.since is yyyy-MM-dd HH:mm.")
                .displayName("Since")
                .validator(
                        (ConfigDef.Validator)Validators.dateTimeValidator("yyyy-MM-dd HH:mm", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))

                .group("Jira")
                .importance(ConfigDef.Importance.MEDIUM)
                .width(ConfigDef.Width.MEDIUM)
                .end();
        configKeys
                .define("jira.tables", ConfigDef.Type.LIST)
                .noDefaultValue()
                .documentation("The tables that are to be extracted and written to Kafka.")
                .displayName("Jira Tables")
                .validator((ConfigDef.Validator)Validators.oneOf(JiraEntity.class))
                .recommender(Recommenders.anyOf(new Object[] { JiraEntity.class })).group("Jira")
                .importance(ConfigDef.Importance.HIGH)
                .width(ConfigDef.Width.LONG)
                .end();
        HttpOperationConfig.addEntities(configKeys, "Jira");
        configKeys.get("entity.names")
                .defaultValue("")
                .validator((ConfigDef.Validator)Validators.notNull())
                .recommender(Recommenders.visibleIf("entity.names", value -> false))
                .internal(true);
        configKeys
                .define("jira.username", ConfigDef.Type.STRING)
                .noDefaultValue()
                .documentation("The username to be used to authenticate with Jira.")
                .displayName("Jira Username")
                .validator((ConfigDef.Validator)Validators.nonEmptyString())
                .group("Authorization")
                .importance(ConfigDef.Importance.HIGH)
                .width(ConfigDef.Width.LONG)
                .end();
        configKeys
                .define("jira.api.token", ConfigDef.Type.PASSWORD)
                .noDefaultValue()
                .documentation("The password to be used to authenticate with Jira")
                .displayName("Jira Token")
                .validator((ConfigDef.Validator)Validators.notNull())
                .group("Authorization")
                .importance(ConfigDef.Importance.HIGH)
                .width(ConfigDef.Width.LONG)
                .end();
        HttpOperationConfig.addAuthorization(configKeys, "Authorization");
        configKeys.get("auth.type")
                .defaultValue(AuthType.BASIC.name().toLowerCase())
                .recommender(Recommenders.visibleIf("auth.type", value -> false))
                .internal(true);
        configKeys.get("bearer.token")
                .recommender(Recommenders.visibleIf("bearer.token", value -> false))
                .internal(true);
        configKeys.alter(key -> key.internal(true), key -> key.name().contains("connection."));
        configKeys.alter(key -> key.internal(true), key -> key.name().contains("oauth2."));
        configKeys
                .define("jira.url", ConfigDef.Type.STRING)
                .noDefaultValue()
                .documentation("Jira Service Url.")
                .displayName("Jira Endpoint")
                .validator((ConfigDef.Validator)Validators.validUri(new String[] { "http", "https" })).group("Jira")
                .importance(ConfigDef.Importance.HIGH)
                .width(ConfigDef.Width.LONG)
                .end();
        HttpOperationConfig.addServiceUrl(configKeys, "Jira");
        configKeys.get("url")
                .defaultValue("http://localhost:8080")
                .recommender(Recommenders.visibleIf("url", value -> false))
                .internal(true);
    }

    public String serviceUrl() {
        return getString("url").concat("/rest/api/2");
    }

    public Date since() {
        return this.since;
    }
}
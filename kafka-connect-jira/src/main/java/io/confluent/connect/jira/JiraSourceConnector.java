package io.confluent.connect.jira;

import io.confluent.connect.jira.utils.JiraUtils;
import io.confluent.connect.operations.rest.RestServiceSourceConnector;
import io.confluent.connect.utils.Version;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraSourceConnector extends RestServiceSourceConnector<JiraSourceConnectorConfig> {
    private static final Logger log = LoggerFactory.getLogger(JiraSourceConnector.class);

    protected JiraSourceConnectorConfig createConfig(Map<String, String> settings) {
        return new JiraSourceConnectorConfig(settings);
    }

    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<String> entityNames = new ArrayList<>(((JiraSourceConnectorConfig)connectorConfig()).entityNames());
        List<Map<String, String>> taskconfigs = new ArrayList<>();
        JiraUtils.taskPartitionsForEntities(JiraUtils.getJiraEntites(entityNames), maxTasks)
                .forEach(list -> taskconfigs.add(taskConfigsForJira(list)));
        return taskconfigs;
    }

    public Class<? extends Task> taskClass() {
        return (Class)JiraSourceTask.class;
    }

    public ConfigDef config() {
        return JiraSourceConnectorConfig.config().toConfigDef();
    }

    public String version() {
        return Version.forClass(getClass());
    }

    private Map<String, String> taskConfigsForJira(List<JiraEntity> entities) {
        List<String> entityNames = JiraUtils.getEntites(entities);
        Map<String, String> taskConfig = ((JiraSourceConnectorConfig)connectorConfig()).originalsStrings();
        String entityNamesAsString = String.join(",", (Iterable)entityNames);
        taskConfig.put("jira.tables", entityNamesAsString);
        log.debug("Generating task configuration for entities: {}", entityNamesAsString);
        return taskConfig;
    }
}

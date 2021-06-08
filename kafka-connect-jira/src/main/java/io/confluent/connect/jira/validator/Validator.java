package io.confluent.connect.jira.validator;

import java.util.Collections;
import java.util.List;

import io.confluent.connect.jira.JiraEntity;
import io.confluent.connect.jira.utils.JiraUtils;
import org.apache.kafka.common.config.ConfigException;

public class Validator {
    public static void validateTables(List<String> entities) {
        List<JiraEntity> jiraEntities = JiraUtils.getJiraEntites(entities);
        if (!jiraEntities.contains(JiraEntity.PROJECTS) &&
                !Collections.disjoint(jiraEntities, JiraEntity.PROJECT_DEPENDENT_ENTITIES))
            throw new ConfigException("jira.tables should include " + JiraEntity.PROJECTS);
        if (!jiraEntities.contains(JiraEntity.ISSUES) &&
                !Collections.disjoint(jiraEntities, JiraEntity.ISSUE_DEPENDENT_ENTITIES))
            throw new ConfigException("jira.tables should include " + JiraEntity.ISSUES);
    }

    public static void validateTokenConfig(String jiraToken) {
        if (jiraToken.isEmpty())
            throw new ConfigException("jira.api.token should not be empty.");
    }
}
package io.confluent.connect.jira;

import io.confluent.connect.utils.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum JiraEntity {
    WORKLOGS, PROJECT_TYPES, PROJECT_CATEGORIES, ISSUE_COMMENTS, VERSIONS, ISSUES, PROJECTS, ISSUE_TRANSITIONS, ROLES, USERS, RESOLUTIONS, CHANGELOGS;

    public static final List<JiraEntity> ISSUE_DEPENDENT_ENTITIES;

    public static final List<JiraEntity> PROJECT_DEPENDENT_ENTITIES;

    public static final List<JiraEntity> ALL_DEPENDENT_ENTITIES;

    static {
        ISSUE_DEPENDENT_ENTITIES = ImmutableList.of((JiraEntity[]) new JiraEntity[]{ISSUE_TRANSITIONS, CHANGELOGS, WORKLOGS, RESOLUTIONS, ISSUE_COMMENTS});
        PROJECT_DEPENDENT_ENTITIES = ImmutableList.of((JiraEntity[]) new JiraEntity[]{VERSIONS});
        ALL_DEPENDENT_ENTITIES = (List<JiraEntity>) Stream.concat(ISSUE_DEPENDENT_ENTITIES.stream(), PROJECT_DEPENDENT_ENTITIES.stream()).distinct().collect(Collectors.toList());
    }

    public static JiraEntity toEnum(String name) {
        return valueOf(name.toUpperCase());
    }
}

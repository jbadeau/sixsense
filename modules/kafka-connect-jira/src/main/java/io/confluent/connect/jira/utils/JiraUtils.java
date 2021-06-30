package io.confluent.connect.jira.utils;

import io.confluent.connect.jira.JiraEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JiraUtils {
    public static List<List<JiraEntity>> taskPartitionsForEntities(List<JiraEntity> entities, int numTasks) {
        List<JiraEntity> independentEntities = (List<JiraEntity>)entities.stream().filter(entity -> !JiraEntity.ALL_DEPENDENT_ENTITIES.contains(entity)).collect(Collectors.toList());
        numTasks = (independentEntities.size() < numTasks) ? independentEntities.size() : numTasks;
        if (numTasks <= 0)
            throw new IllegalArgumentException("Number of tasks must be positive.");
        List<List<JiraEntity>> results = new ArrayList<>(numTasks);
        int perGroup = independentEntities.size() / numTasks;
        int leftover = independentEntities.size() % numTasks;
        int assigned = 0;
        for (int group = 0; group < numTasks; group++) {
            int numThisGroup = (group < leftover) ? (perGroup + 1) : perGroup;
            List<JiraEntity> groupList = new ArrayList<>(numThisGroup);
            for (int i = 0; i < numThisGroup; i++) {
                groupList.add(independentEntities.get(assigned));
                assigned++;
            }
            results.add(groupList);
        }
        return addDependentEntitiesToTheTask(entities, results);
    }

    private static List<List<JiraEntity>> addDependentEntitiesToTheTask(List<JiraEntity> entities, List<List<JiraEntity>> results) {
        List<JiraEntity> issueDependentGroup = (List<JiraEntity>)entities.stream().filter(entity -> JiraEntity.ISSUE_DEPENDENT_ENTITIES.contains(entity)).collect(Collectors.toList());
        List<JiraEntity> projectDependentGroup = (List<JiraEntity>)entities.stream().filter(entity -> JiraEntity.PROJECT_DEPENDENT_ENTITIES.contains(entity)).collect(Collectors.toList());
        results.forEach(result -> {
            if (result.contains(JiraEntity.ISSUES) && !issueDependentGroup.isEmpty())
                result.addAll(issueDependentGroup);
            if (result.contains(JiraEntity.PROJECTS) && !projectDependentGroup.isEmpty())
                result.addAll(projectDependentGroup);
        });
        return results;
    }

    public static List<JiraEntity> getJiraEntites(List<String> entities) {
        return (List<JiraEntity>)entities.stream().map(entity -> JiraEntity.valueOf(entity.toUpperCase()))
                .collect(Collectors.toList());
    }

    public static List<String> getEntites(List<JiraEntity> entities) {
        return (List<String>)entities.stream().map(entity -> entity.name().toLowerCase())
                .collect(Collectors.toList());
    }
}

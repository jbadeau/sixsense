import {Entity} from '@backstage/catalog-model';

export const JIRA_PROJECT_KEY_ANNOTATION = 'jira/project-key';

export const useProjectEntity = (entity: Entity) => {
    return {
        projectKey: entity.metadata?.annotations?.[
            JIRA_PROJECT_KEY_ANNOTATION
            ] as string
    }
};
import {createApiFactory, createComponentExtension, createPlugin, discoveryApiRef,} from '@backstage/core-plugin-api';

import {rootRouteRef} from './routes';
import {ApiClient, apiRef} from "./api/client";


export const jiraMetricsPlugin = createPlugin({
    id: 'jira-metrics',
    routes: {
        root: rootRouteRef,
    },
    apis: [
        createApiFactory({
            api: apiRef,
            deps: {discoveryApi: discoveryApiRef},
            factory: ({discoveryApi }) => new ApiClient(discoveryApi),
        }),
    ],
});

export const EntityWasteCard = jiraMetricsPlugin.provide(
    createComponentExtension({
        component: {
            lazy: () => import('./components/waste').then((m) => m.WasteComponent)
        },
    })
);
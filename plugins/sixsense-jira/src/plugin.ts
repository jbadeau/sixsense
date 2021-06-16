import { createPlugin, createRoutableExtension } from '@backstage/core';

import { rootRouteRef } from './routes';

export const sixsenseJiraPlugin = createPlugin({
  id: 'sixsense-jira',
  routes: {
    root: rootRouteRef,
  },
});

export const SixsenseJiraPage = sixsenseJiraPlugin.provide(
  createRoutableExtension({
    component: () =>
      import('./components/ExampleComponent').then(m => m.ExampleComponent),
    mountPoint: rootRouteRef,
  }),
);

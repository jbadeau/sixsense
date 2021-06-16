import React from 'react';
import { createDevApp } from '@backstage/dev-utils';
import { sixsenseJiraPlugin, SixsenseJiraPage } from '../src/plugin';

createDevApp()
  .registerPlugin(sixsenseJiraPlugin)
  .addPage({
    element: <SixsenseJiraPage />,
    title: 'Root Page',
  })
  .render();

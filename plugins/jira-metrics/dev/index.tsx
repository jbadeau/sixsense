import React from 'react';
import { createDevApp } from '@backstage/dev-utils';
import { jiraMetricsPlugin, JiraMetricsPage } from '../src/plugin';

createDevApp()
  .registerPlugin(jiraMetricsPlugin)
  .addPage({
    element: <JiraMetricsPage />,
    title: 'Root Page',
    path: '/jira-metrics'
  })
  .render();

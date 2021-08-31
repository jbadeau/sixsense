import { createDevApp } from '@backstage/dev-utils';
import { jiraMetricsPlugin } from '../src/plugin';

createDevApp()
  .registerPlugin(jiraMetricsPlugin)
  .render();

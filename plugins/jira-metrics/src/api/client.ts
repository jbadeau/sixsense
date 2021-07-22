import {createApiRef, DiscoveryApi} from '@backstage/core-plugin-api';
import {Api} from './api';
import {csvToTable} from '@influxdata/influxdb-client-giraffe'
import {newTable, Table} from "@influxdata/giraffe";

export const apiRef = createApiRef<Api>({
    id: 'plugin.jira-metrics.service',
    description: 'Used by the Jira Metrics plugin to make requests',
});

export class ApiClient implements Api {

    constructor(private readonly discoveryApi: DiscoveryApi) {
    }

    async getWaste(projectKey: string): Promise<Table> {
        const baseUrl = await this.discoveryApi.getBaseUrl('proxy');
        const response = await fetch(
            `${baseUrl}/influxdb/api/api/v2/query?org=${projectKey.toLowerCase()}`,
            {
                method: 'POST',
                body: JSON.stringify(
                    {
                        "query": "from(bucket:\"jira-issues-waste\") |> range(start: -6mo) |> group(columns: [\"_value\", \"_time\"])",
                        "dialect": {
                            "header": true,
                            "delimiter": ",",
                            "quoteChar": "'",
                            "commentPrefix": "#",
                            "annotations": ["datatype", "group", "default"]
                        },
                        "type": "flux"
                    }
                )
            }
        );
        return await response.text().then(txt => {
            return csvToTable(txt, newTable);
        })
    }

}
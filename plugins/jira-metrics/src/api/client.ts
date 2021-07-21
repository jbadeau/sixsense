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
                body: `from(bucket: "jira-issues-waste")
                    |> range(start: -6mo)
                    |> group(columns: ["_value", "_time"])`
            }
        );
        return await response.text().then(txt => {
            let csv = `#group,false,false,false,false,true,true,false,false,false
#datatype,string,long,dateTime:RFC3339,dateTime:RFC3339,dateTime:RFC3339,string,string,string,string
#default,_result,,,,,,,,
${txt}`
            return csvToTable(csv, newTable);
        })
    }

}
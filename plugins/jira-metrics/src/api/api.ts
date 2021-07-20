import {Table} from "@influxdata/giraffe/src/types/index";

export interface Api {

    getWaste(projectKey: string,): Promise<Table>;

}
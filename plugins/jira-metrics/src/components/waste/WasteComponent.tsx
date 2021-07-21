import React from 'react';
import {useEntity} from "@backstage/plugin-catalog-react";
import {Config, Plot, RAINBOW_EIGHT, timeFormatter} from "@influxdata/giraffe";
import {useWaste} from "../../hooks";
import {useProjectEntity} from "../../hooks/useProjectEntity";

export const WasteComponent = () => {
    const {entity} = useEntity();
    const {projectKey} = useProjectEntity(entity);
    const {waste, wasteLoading, wasteError} = useWaste(projectKey);
    const config: Config = {
        table: waste,
        valueFormatters: {
            _time: timeFormatter({timeZone: 'UTC', format: 'MMMM'}),
        },
        layers: [{type: 'histogram', x: "_time",   fill: ['_value'], colors: RAINBOW_EIGHT}]
    };
    // result,table,_start,_stop,_time,_value,_field,_measurement,key
    return !wasteLoading && !wasteError ? <Plot config={config}/> : <div>Not ready</div>
};

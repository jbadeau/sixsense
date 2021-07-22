import React from 'react';
import {useEntity} from "@backstage/plugin-catalog-react";
import {Config, Plot, RAINBOW_EIGHT, timeFormatter} from "@influxdata/giraffe";
import Alert from '@material-ui/lab/Alert';
import {createStyles, makeStyles, Theme} from '@material-ui/core';
import {InfoCard, Progress,} from '@backstage/core-components';
import {useWaste} from "../../hooks";
import {useProjectEntity} from "../../hooks/useProjectEntity";

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        infoCard: {
            marginBottom: theme.spacing(3),
            '& + .MuiAlert-root': {
                marginTop: theme.spacing(3),
            },
        },
        root: {
            height: '605px',
            flexGrow: 1,
            fontSize: '0.75rem',
            '& > * + *': {
                marginTop: theme.spacing(1),
            },
        },
    }),
);

export const WasteComponent = () => {
    const classes = useStyles();
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
    return <InfoCard title={'Waste (withdrawn items)'} className={classes.infoCard}>
        <div className={classes.root}>
        {wasteLoading ? <Progress /> : null}
        {wasteError ? (<Alert severity="error">{wasteError.message}</Alert>) : null}
        {waste ? (<Plot config={config}/>) : null}
        </div>
    </InfoCard>
};

import {newTable} from '@influxdata/giraffe'

const WASTE_TIME_COL = [
    1639325014000,
    1636733014000,
    1634054614000,
    1631462614000,
    1628784214000,
    1626105814000,
    1623513814000,
    1620835414000,
    1618243414000,
    1615565014000,
    1613145814000,
    1610467414000,
    1609517014000
]

const WASTE_VALUE_COL = [
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    2
]

const WASTE_LAST_STATUS_COL = [
    'Test',
    'Committed',
    'Committed',
    'Committed',
    'Committed',
    'Committed',
    'Test',
    'Committed',
    'Committed',
    'Committed',
    'Test',
    'Committed',
    'Test',
]

const WASTE_RESOLUTION_COL = [
    'Withdrawn',
    'Cancelled',
    'Cancelled',
    'Withdrawn',
    'Withdrawn',
    'Cancelled',
    'Withdrawn',
    'Cancelled',
    'Cancelled',
    'Cancelled',
    'Withdrawn',
    'Cancelled',
    'Cancelled',
]

export const Waste = newTable(13)
    .addColumn('_time', 'dateTime:RFC3339', 'time', WASTE_TIME_COL)
    .addColumn('_value', 'system', 'number', WASTE_VALUE_COL)
    .addColumn('last_status', 'string', 'string', WASTE_LAST_STATUS_COL)
    .addColumn('resolution', 'string', 'string', WASTE_RESOLUTION_COL)
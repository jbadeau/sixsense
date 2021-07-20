import {useCallback} from 'react';
import {useApi} from '@backstage/core-plugin-api';
import {useAsync} from 'react-use';
import {handleError} from './utils';
import {apiRef} from '../api';

export const useWaste = (projectKey: string) => {
    const api = useApi(apiRef);

    const getWaste = useCallback(async () => {
        try {
            return await api.getWaste(projectKey);
        } catch (err) {
            return handleError(err);
        }
    }, [api, projectKey]);

    const {loading, value, error} = useAsync(() => getWaste(), []);
    return {
        wasteLoading: loading,
        waste: value,
        wasteError: error,
    };

};
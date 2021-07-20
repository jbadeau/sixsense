export interface Config {
    app: {
        /**
         * Frontend root URL
         * @visibility frontend
         */
        baseUrl: string;

        /**
         * The title of the app, as shown in the Backstage web interface.
         * @visibility frontend
         */
        title?: string;
    };
}
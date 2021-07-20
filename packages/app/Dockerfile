FROM nginx:mainline

RUN apt-get update && apt-get -y install jq && rm -rf /var/lib/apt/lists/*

COPY packages/app/dist /usr/share/nginx/html
COPY packages/app/default.conf.template /etc/nginx/templates/default.conf.template

USER root
RUN chmod -R 777 /etc/nginx && chmod -R 777 /var/log/nginx && chmod -R 777 /var/cache/nginx && chmod -R 777 /var/run && chmod -R 777 /usr/share/nginx/html
USER nginx

ENV PORT 8080
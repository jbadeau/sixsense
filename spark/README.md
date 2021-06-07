#spark

1/ `helm repo add bitnami https://charts.bitnami.com/bitnami`
2/ update values.yaml to deactivate securityContext 
2/ `helm install spark --values values.yaml bitnami/spark`

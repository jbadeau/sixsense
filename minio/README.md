#minio

1/ $ `helm repo add minio https://helm.min.io/`

2/ Review the values.yaml that are used to override minio defaults

3/ $ `helm install minio --values ../values.yaml .`

4/ create route by `oc expose svc/minio`

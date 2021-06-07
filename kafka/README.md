#kafka strimzi operator
1/ `helm repo add strimzi https://strimzi.io/charts/`

2/ `helm install strimzi/strimzi-kafka-operator`

3/ apply CR kafka defining the kafka cluster `oc apply -f kafka.yaml`


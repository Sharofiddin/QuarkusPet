eval $(minikube -p minikube docker-env)
./mvnw clean package -Dquarkus.kubernetes.deploy=true

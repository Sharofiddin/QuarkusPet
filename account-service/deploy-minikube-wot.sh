eval $(minikube -p minikube docker-env)
mvn clean package -Dquarkus.kubernetes.deploy=true -DskipTests

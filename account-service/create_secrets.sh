kubectl create secret generic db-credentials \
        --from-literal=username=admin \
        --from-literal=password=secret \
        --from-literal=db.username=quarkus_banking \
        --from-literal=db.password=quarkus_banking
#!/bin/bash

# A simple script to run all tests in
# an infinite loop to generate a "busy"
# dashboard
# Press CTRL-C to exit script

ACCOUNT_URL=`minikube service --url account-service`

while [ 1 ];
do
  ./concurrent.sh
  ./force_multiple_fallbacks.sh
  ./invoke_deposit_endpoints.sh
  ./overload_bulkhead.sh

  count=0

  while (( count++ < 15 )); do
  curl -i $ACCOUNT_URL/accounts/745/balance
  done
done

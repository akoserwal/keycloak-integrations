#!/bin/bash

RESULT=`curl -k --data "grant_type=client_credentials&client_id=fastapi-client&client_secret=Gbpnp5W0by7xmyuFcnIcC6GCtkPVgeXL" http://localhost:8080/realms/fastapi-realm/protocol/openid-connect/token`

if command -v jq > /dev/null 2>&1; then
  export ACCESS_TOKEN=$(echo "${RESULT}" | jq -r .access_token)
  printf "%s\n" ${ACCESS_TOKEN}
else
  echo $RESULT
fi
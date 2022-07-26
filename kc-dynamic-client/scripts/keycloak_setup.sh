#!/bin/bash

set -e

docker network create kc-network || true

docker run \
  --name=kc \
  --net kc-network \
  -p 8180 \
  -e DB_VENDOR=h2  \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  -d quay.io/keycloak/keycloak:17.0.1-legacy

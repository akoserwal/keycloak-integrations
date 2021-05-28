#!/bin/bash

set -e

docker network create keycloak-postgres-network || true

docker run \
  --name=keycloak-postgres \
  --net keycloak-postgres-network \
  -e POSTGRES_PASSWORD=kcadmin \
  -e POSTGRES_USER=kcadmin \
  -e POSTGRES_DB=keycloak \
  -p 32769:5432 \
  -d postgres:13
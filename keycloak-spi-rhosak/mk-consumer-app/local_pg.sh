#!/bin/bash

set -e

docker network create sa-consumer-network || true

docker run \
  --name=sa-consumer-db \
  --net sa-consumer-network \
  --restart=always \
  -e POSTGRES_PASSWORD="quarkus_test" \
  -e POSTGRES_USER="quarkus_test" \
  -e POSTGRES_DB="quarkus_test" \
  -p 5432:5432 \
  -d postgres:13
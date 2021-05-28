#!/bin/bash


set -e

docker stop keycloak-postgres

docker rm keycloak-postgres

docker network rm keycloak-postgres-network
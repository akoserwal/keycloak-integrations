#!/bin/bash

set -e

docker stop sa-consumer-db

docker rm sa-consumer-db

docker network rm sa-consumer-db-network
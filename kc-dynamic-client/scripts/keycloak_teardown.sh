#!/bin/bash

set -e

docker stop kc

docker rm kc

docker network rm kc-network

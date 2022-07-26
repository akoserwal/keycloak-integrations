set -e

docker stop kc-test-db

docker rm kc-test-db

docker network rm kc-test-network
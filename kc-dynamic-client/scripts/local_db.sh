
set -e

docker network create kc-test-network || true

docker run \
  --name=kc-test-db \
  --net kc-test-network \
  --restart=always \
  -e POSTGRES_PASSWORD=$(cat secrets/db.password) \
  -e POSTGRES_USER=$(cat secrets/db.user) \
  -e POSTGRES_DB=$(cat secrets/db.name) \
  -p $(cat secrets/db.port):5432 \
  -d postgres:13
# Setup a Keycloak instance with default realm and a service account

Current step uses Keycloak development mode and sutiable to testing with a test realm name `test-realm` and a service account `test-sa`.

## Run

```docker compose up -d```

Start a keycloak instance running at `localhost:8084`.

* Imports the test-realm during the startup [realm-export.json](./realm-export.json)
* Sets up service account [test-sa.json](./test-sa.json)
* Script to request for an access token using service account client id and secret [get-token.sh](./get-token.sh)

Request for an access token
```
./get-token.sh 
```
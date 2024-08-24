# Securing FastAPI with Keycloak

## Virtual env
`make virtual`

## Run
`make run`

## Run Keycloak
`make keycloak`

## Get Token
`make token`

## Sample calls

`export TOKEN=`

```
curl http://127.0.0.1:8000/secure-data \
--header "Authorization: Bearer ${TOKEN}"
```
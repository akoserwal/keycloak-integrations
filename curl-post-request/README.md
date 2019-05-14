# keycloak.sh Shell Script to get JWT token using curl

```
Usage: . ./keycloak-curl.sh hostname realm username clientid
  options:
    hostname: localhost:8081
    realm:keycloak-demo
    clientid:demo
    For verify ssl: use 'y' (otherwise it will send curl post with --insecure)

```

Sample Request 
```
./keycloak-curl.sh 0.0.0.0:8445 keycloak-demo jwick vue-test-app 

```

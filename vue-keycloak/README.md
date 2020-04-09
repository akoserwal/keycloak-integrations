# vue-keycloak

Read: https://medium.com/keycloak/secure-vue-js-app-with-keycloak-94814181e344

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Run your tests
```
npm run test
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

## Keycloak Docker run

### Docker for Windows 

Make sure the Docker Resource -> Filesharing is active for the drive you cloned the project

```bash
docker run -p 8081:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -v %CD:\=/%/keycloak-config/realm-export.json:/tmp/realm-export.json -e KEYCLOAK_IMPORT=/tmp/realm-export.json --name keycloak-vue-test jboss/keycloak
```
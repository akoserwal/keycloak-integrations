# Secure Spring-Boot Application using Keycloak

Using `keycloak-spring-boot-2-adapter`

* Keycloak Service: http://127.0.0.1:8080/auth
* REALM: keylcoak deam
* client: spring-boot-mvc-app


# Configure in the application.properties

```
keycloak.realm = keycloak-demo
keycloak.auth-server-url = http://127.0.0.1:8080/auth
keycloak.resource = spring-boot-mvc-app
keycloak.principal-attribute=preferred_username
keycloak.public-client=true


spring.main.allow-bean-definition-overriding=true
server.port=8081
```


# Build & Run
`mvn clean spring-boot:run`
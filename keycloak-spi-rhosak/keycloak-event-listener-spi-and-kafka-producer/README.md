# Keycloak: events listener spi & publish events to Red Hat OpenShift Streams for Apache Kafka

* Publishes Login Events & Admin events to Kafka instance
* Published specific events
  * "CLIENT_LOGIN"
  * "CLIENT"

But it can use to publish any event types.
  
# Setup
Configure application.properties
```
bootstrap=
#SASL/OAUTHBEARER
sasl.mechanism=OAUTHBEARER
OAUTH_TOKEN_ENDPOINT_URI=https://identity.api.openshift.com/auth/realms/rhoas/protocol/openid-connect/token
OAUTH_CLIENT_ID=
OAUTH_CLIENT_SECRET=

```

## build 
    `mvn clean install`

# Deploy to Keycloak instance

## Keycloak 17 or higher
 
 `cp keycloak-event-listener-spi-and-kafka-producer.jar  /keycloak-x.x.x/providers
 `
  
## Restart the keycloak server
`./kc.sh start-dev --http-port 8181 --spi-event-listener-keycloak-custom-event-listener-enabled=true --spi-event-listener-keycloak-custom-event-listener=keycloak-custom`
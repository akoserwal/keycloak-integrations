# KEYCLOAK EVENTS KAFKA CONSUMER APPLICATION

Consumer application works with Red Hat OpenShift Streams for Apache Kafka
* Listen for topics: CLIENT/CLIENT_LOGIN
* Stores in the postgres database
* Exposes a Rest API
http://localhost:8081/service_accounts/
http://localhost:8081/service_accounts_logins/

# Create a local Postgresdb using docker

`./local_pg.sh`

Create Tables

```roomsql
CREATE TABLE public.serviceaccounts (
	client_id varchar(255) NOT NULL,
	operation varchar(50) NULL,
	count int8 NULL DEFAULT 0,
	ip varchar(255) NULL,
	status varchar(255) NULL,
	created_at timestamp NULL DEFAULT LOCALTIMESTAMP,
	last_update_at timestamp NULL DEFAULT LOCALTIMESTAMP,
	CONSTRAINT serviceaccounts_pkey PRIMARY KEY (client_id)
);


CREATE TABLE serviceaccounts_logins (
	client_id varchar(80) NOT NULL,
	login_timestamp timestamp NULL DEFAULT LOCALTIMESTAMP
);
```


# Configure the application.properties

```
# Configuration file
# key = value

quarkus.http.port=8081
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=quarkus_test
quarkus.datasource.password=quarkus_test
quarkus.datasource.reactive.url=postgresql://localhost:5432/quarkus_test


consumer.bootstrap.url=
consumer.oauth.clientid=
consumer.oauth.secret=
consumer.oauth.token.url=https://identity.api.openshift.com/auth/realms/rhoas/protocol/openid-connect/token
consumer.group.id.config=svc-consumer-test

```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```


# teardown db

`./teardown_pg.sh`


## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `mk-consumer-app-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/mk-consumer-app-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/mk-consumer-app-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
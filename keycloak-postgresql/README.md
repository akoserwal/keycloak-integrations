# Running keycloak with PostgresSql database

# Prerequisites

* Docker
* Keycloak

# Prepare Module

## Download PostgreSql driver and copy the drive to module directory
``` 
cd keycloak-config/postgres/main && { wget https://jdbc.postgresql.org/download/postgresql-42.2.20.jar ; cd -; } 
```

## Copy the module directory to the Keycloak Installation directory

Set your Keycloak installation directory path

Example
```
export KEYCLOAK_DIR=~/keycloak-13.0.1
```
Copy the postgres module to Keycloak Installation modules directory path

```
rsync -r keycloak-config/* $KEYCLOAK_DIR/modules/system/layers/keycloak/com
```

# Configure datasource in Keycloak

```
cd $KEYCLOAK_DIR/standalone/configuration
```

#### Open the standalone.xml in editor

Add the datasource config

* make sure the connection url is correct, In my case it is `jdbc:postgresql://localhost:32769/keycloak`
* update the POSTGRES_USER & `<user-name></user-name>`
* update the POSTGRES_PASSWORD & `<password></password>`

Make sure datasource config should matches with [setup_postgres.sh](setup_postgres.sh)

```
                <datasource jndi-name="java:jboss/datasources/KeycloakDS" pool-name="KeycloakDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
                    <connection-url>jdbc:postgresql://localhost:32769/keycloak</connection-url>
                    <driver>postgres</driver>
                    <security>
                        <user-name>kcadmin</user-name>
                        <password>kcadmin</password>
                    </security>
                </datasource>
```

Add the Driver config

```
                   <driver name="postgres" module="com.postgres">
                        <driver-class>org.postgresql.Driver</driver-class>
                        <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
                    </driver> 

```

Replace the `ExampleDS` with `KeycloakDS`

```
<default-bindings context-service="java:jboss/ee/concurrency/context/default" datasource="java:jboss/datasources/KeycloakDS" managed-executor-service="java:jboss/ee/concurrency/executor/default" managed-scheduled-executor-service="java:jboss/ee/concurrency/scheduler/default" managed-thread-factory="java:jboss/ee/concurrency/factory/default"/>
```

Refer: Sample [standalone.xml](standalone.xml)


# Run

## Start the PostgresSql DB

Update the 
* POSTGRES_USER
* POSTGRES_PASSWORD

```
./setup_postgres.sh
```


## Start the Keycloak Server

```
cd $KEYCLOAK_DIR/bin
./standalone.sh
```

# Keycloak Dynamic Client Registration

Run Keycloak instance port
127.0.0.1:8180

`make keycloak/setup`

Login to admin console: http://127.0.0.1:8180/auth
user=admin 
password=admin

Create a demo realm: demo

# Create a local postgresql db
make db/setup

# Create Table
```sql

-- Drop table

-- DROP TABLE public.clients;

CREATE TABLE public.clients (
	client_id varchar(100) NULL,
	registration_token text NULL
);
```
# Run the application
go run pkg/main.go  
ifeq ($(DOCKER),)
DOCKER:=$(shell command -v podman || command -v docker)
endif


.PHONY: virtual
virtual:
	poetry shell

.PHONY: run
run:
	fastapi dev fastapi_keycloak/main.py

.PHONY: token
token:
	./token.sh

.PHONY: keycloak
keycloak:
	${DOCKER} compose -f ./docker-compose.yaml up -d
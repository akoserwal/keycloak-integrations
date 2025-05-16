# Securing MCP Server-Client Architecture with Keycloak Using Ollama, LLaMA Stack in Python

As AI workloads scale in sensitivity and compute cost, it's crucial to secure interactions between clients and inference servers. This post demonstrates how to secure a Python-based MCP server-client architecture using Ollama and the LLaMA model stack, protected via OpenID Connect (OIDC) using Keycloak.

## Prerequisites

* Python 3.10+
* Docker & Docker Compose
* Keycloak 25+
* Ollama installed (ollama serve)
* Keycloak Python client: python-keycloak
* HTTP libraries: requests, httpx


## Run Keycloak Locally

```sh
docker run -p 8081:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.2.4 start-dev
```

## Run ollama

```ollama run llama3.2:3b --keepalive 60m```


## Run llama stack (optional)

```INFERENCE_MODEL=llama3.2:3b uv run --with llama-stack llama stack build --template ollama --image-type venv --run```

## Run MCP Server and Client

Setup virtual environment

```sh
uv venv
source .venv/bin/activate
```

Add Python dependencies

```sh
 uv add fastmcp python-keycloak 
```

### Run FastMCP Server

```sh
uv run mcpserver.py
```

### Run FastMCP Client

```sh
uv run client.py
```
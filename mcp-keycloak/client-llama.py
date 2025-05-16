import asyncio
import json
from fastmcp import Client
from fastmcp.client.transports import SSETransport
from keycloak import KeycloakOpenID

# Keycloak configuration
KEYCLOAK_URL = "http://localhost:8081"
REALM_NAME = "demo-mcp"
CLIENT_ID = "mcp-secure"
CLIENT_SECRET = "6UJzbvU6H29BeiiEUx6f4lfqKFzMu9nD"

# FastMCP API endpoint
FASTMCP_URL = "http://127.0.0.1:8001"

def get_access_token():
    """Get access token from Keycloak"""
    keycloak_openid = KeycloakOpenID(
        server_url=KEYCLOAK_URL,
        realm_name=REALM_NAME,
        client_id=CLIENT_ID,
        client_secret_key=CLIENT_SECRET
    )
    
    # Get token (using client credentials flow)
    token = keycloak_openid.token(grant_type="client_credentials")
    return token["access_token"]

async def list_available_tools():
    """List all available tools on the server"""
    token = get_access_token()
    headers = {"Authorization": f"Bearer {token}"}
    async with Client(
        transport=SSETransport(f"{FASTMCP_URL}/", headers)
    ) as client:
        tools = await client.list_tools()
        print("Available tools:")
        for tool in tools:
            #print(f"- {tool['name']}: {tool['description']}")
            print(f"{tool}")
        return tools

async def call_add(a: int, b: int):
    """Call the add tool"""
    token = get_access_token()
    headers = {"Authorization": f"Bearer {token}"}
    async with Client(
        transport=SSETransport(f"{FASTMCP_URL}/", headers)
    ) as client:
        result = await client.call_tool("add", {"a": a, "b": b})
        print(f"Addition result: {a} + {b} = {result}")
        return result

async def call_generate_text(prompt: str, model: str = "llama2", temperature: float = 0.7):
    """Call the Ollama text generation tool"""
    token = get_access_token()
    headers = {"Authorization": f"Bearer {token}"}
    async with Client(
        transport=SSETransport(f"{FASTMCP_URL}/", headers)
    ) as client:
        print(f"Generating text with model '{model}'...")
        result = await client.call_tool("generate_text", {
            "prompt": prompt,
            "model": model,
            "temperature": temperature
        })
        print("\nGenerated Text:")
        print(result)
        return result
    
async def call_list_ollama_models():
        token = get_access_token()
        headers = {"Authorization": f"Bearer {token}"}
        async with Client(
         transport=SSETransport(f"{FASTMCP_URL}/", headers)
         ) as client:
         models = await client.call_tool("list_ollama_models", {})
         print("Available Ollama models:")
         print(models)

async def interactive_demo():
    """Run an interactive demo of all available tools"""
    print("\n=== MCP Client with Ollama Integration ===")
    
    # List available tools
    await list_available_tools()
    
    # # Demo add tool
    print("\n=== Testing Add Tool ===")
    await call_add(5, 3)
    
    # Demo Ollama model listing
    print("\n=== Checking Available Ollama Models ===")
    models = await call_list_ollama_models()

if __name__ == "__main__":
    # Run the interactive demo
    asyncio.run(interactive_demo())
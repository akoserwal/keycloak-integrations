from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from keycloak import KeycloakOpenID
import requests
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Keycloak configuration
KEYCLOAK_SERVER_URL = "http://localhost:8080"
KEYCLOAK_REALM = "fastapi-realm"
KEYCLOAK_CLIENT_ID = "fastapi-client"
KEYCLOAK_CLIENT_SECRET ="None"  # Use None for public clients
ALGORITHM = "RS256"

# Initialize KeycloakOpenID
keycloak_openid = KeycloakOpenID(
    server_url=f"{KEYCLOAK_SERVER_URL}",
    client_id=KEYCLOAK_CLIENT_ID,
    realm_name=KEYCLOAK_REALM,
    client_secret_key=KEYCLOAK_CLIENT_SECRET,
    verify=True
)
config_well_known = keycloak_openid.well_known()

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

def verify_token(token: str = Depends(oauth2_scheme)):
    try:
        decoded_token = keycloak_openid.decode_token(
            token,validate=True,
        )
        username = decoded_token['preferred_username']
        logger.info(f"Decoded token: {decoded_token}")
        # Verify the issuer claim
        issuer = decoded_token["iss"]

        expected_issuer = f"{KEYCLOAK_SERVER_URL}/realms/{KEYCLOAK_REALM}"
        if issuer != expected_issuer:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid issuer")
        
        logger.info(f"username: {username}")
        return decoded_token
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token")

def get_current_user(token: str = Depends(oauth2_scheme)):
    """Extracts the user from the token after verification."""
    payload = verify_token(token)
    
    # Retrieve user information from the token payload
    user_id = payload.get("sub")
    username = payload.get("preferred_username")
    email = payload.get("email")
    roles = payload.get("realm_access", {}).get("roles", [])
    
    if not user_id or not username:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User information not found in token")
    
    # Create a user object or dictionary (customize this based on your needs)
    user = {
        "id": user_id,
        "username": username,
        "email": email,
        "roles": roles
    }
    
    return user
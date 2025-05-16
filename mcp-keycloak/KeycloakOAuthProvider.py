import logging
from typing import Optional, List, Dict, Any
from urllib.parse import urlencode, urlparse, urlunparse, parse_qs
from pydantic import AnyHttpUrl
from keycloak import KeycloakOpenID
from keycloak.exceptions import KeycloakError
from mcp.shared.auth import OAuthClientInformationFull, OAuthToken
from mcp.server.auth.provider import (
    AuthorizationParams,
    AuthorizationCode,
    RefreshToken,
    AccessToken,
    RegistrationError,
    AuthorizeError,
    TokenError,
    OAuthAuthorizationServerProvider
)

# Set up logger
logger = logging.getLogger(__name__)

class KeycloakAuthorizationCode(AuthorizationCode):
    pass

class KeycloakRefreshToken(RefreshToken):
    pass

class KeycloakAccessToken(AccessToken):
    pass

class KeycloakOAuthProvider(OAuthAuthorizationServerProvider[
    KeycloakAuthorizationCode,
    KeycloakRefreshToken,
    KeycloakAccessToken
]):
    def __init__(
        self,
        server_url: str,
        realm_name: str,
        client_id: Optional[str] = None,
        client_secret: Optional[str] = None,
        admin_username: Optional[str] = None,
        admin_password: Optional[str] = None
    ):
        self.server_url = server_url
        self.realm_name = realm_name
        self.client_id = client_id
        self.client_secret = client_secret
        
        logger.info(f"Initializing Keycloak provider for realm: {realm_name}")
        logger.debug(f"Server URL: {server_url}")
        logger.debug(f"Client ID: {client_id}")
        
        try:
            self.keycloak_openid = KeycloakOpenID(
                server_url=server_url,
                realm_name=realm_name,
                client_id=client_id,
                client_secret_key=client_secret
            )
            logger.info("Successfully connected to Keycloak server")
        except Exception as e:
            logger.error(f"Failed to initialize Keycloak connection: {str(e)}")
            raise

    async def get_client(self, client_id: str) -> Optional[OAuthClientInformationFull]:
        """Retrieve client information from Keycloak with debug logging"""
        logger.debug(f"Attempting to fetch client: {client_id}")
        try:
            if client_id == self.client_id:
                client_info = OAuthClientInformationFull(
                    client_id=client_id,
                    client_secret=self.client_secret,
                    redirect_uris=[f"{self.server_url}/realms/{self.realm_name}/account"]
                )
                logger.debug(f"Found client: {client_id}")
                return client_info
            logger.warning(f"Client not found: {client_id}")
            return None
        except Exception as e:
            logger.error(f"Error fetching client {client_id}: {str(e)}")
            raise

    async def authorize(
        self, 
        client: OAuthClientInformationFull, 
        params: AuthorizationParams
    ) -> str:
        """Generate Keycloak authorization URL with detailed logging"""
        logger.info(f"Authorizing client: {client.client_id}")
        logger.debug(f"Authorization params: {params}")
        
        try:
            auth_url = (
                f"{self.server_url}/realms/{self.realm_name}"
                f"/protocol/openid-connect/auth"
            )
            
            query_params = {
                "response_type": "code",
                "client_id": client.client_id,
                "redirect_uri": str(params.redirect_uri),
                "state": params.state,
                "scope": " ".join(params.scopes) if params.scopes else "openid",
                "code_challenge": params.code_challenge,
                "code_challenge_method": "S256"
            }
            
            final_url = f"{auth_url}?{urlencode(query_params)}"
            logger.debug(f"Generated authorization URL: {final_url}")
            return final_url
        except Exception as e:
            logger.error(f"Authorization failed: {str(e)}")
            raise AuthorizeError("server_error", str(e))

    async def load_authorization_code(
        self, 
        client: OAuthClientInformationFull, 
        authorization_code: str
    ) -> Optional[KeycloakAuthorizationCode]:
        """Validate authorization code with detailed error reporting"""
        logger.debug(f"Validating authorization code: {authorization_code}")
        
        try:
            token = self.keycloak_openid.token(
                grant_type="authorization_code",
                code=authorization_code,
                redirect_uri=client.redirect_uris[0]
            )
            
            logger.debug("Successfully validated authorization code")
            return KeycloakAuthorizationCode(
                code=authorization_code,
                scopes=token.get("scope", "").split(),
                expires_at=float(token["expires_in"]),
                client_id=client.client_id,
                code_challenge="",  # Should be stored during authorize
                redirect_uri=AnyHttpUrl(client.redirect_uris[0]),
                redirect_uri_provided_explicitly=True
            )
        except KeycloakError as e:
            logger.error(f"Keycloak validation error: {e.response_body}")
            return None
        except Exception as e:
            logger.error(f"Authorization code validation failed: {str(e)}")
            return None

    async def exchange_authorization_code(
        self, 
        client: OAuthClientInformationFull, 
        authorization_code: KeycloakAuthorizationCode
    ) -> OAuthToken:
        """Exchange auth code for tokens with error details"""
        logger.info(f"Exchanging auth code for client: {client.client_id}")
        
        try:
            token = self.keycloak_openid.token(
                grant_type="authorization_code",
                code=authorization_code.code,
                redirect_uri=str(authorization_code.redirect_uri)
            )
            
            logger.debug("Successfully exchanged authorization code")
            return OAuthToken(
                access_token=token["access_token"],
                refresh_token=token["refresh_token"],
                expires_in=token["expires_in"],
                token_type=token["token_type"],
                scope=token.get("scope", "")
            )
        except KeycloakError as e:
            error_msg = f"Keycloak exchange error: {e.response_body}"
            logger.error(error_msg)
            raise TokenError("invalid_grant", error_msg)
        except Exception as e:
            error_msg = f"Token exchange failed: {str(e)}"
            logger.error(error_msg)
            raise TokenError("server_error", error_msg)

    async def load_access_token(self, token: str) -> Optional[KeycloakAccessToken]:
        """Validate access token with debug logging"""
        logger.info("Validating access token")
        logger.info(self.keycloak_openid.certs())
        
        try:
            token_info = self.keycloak_openid.decode_token(
                token,
            )
            
            logger.debug("Access token validation successful")
            return KeycloakAccessToken(
                token=token,
                client_id=token_info.get("azp", ""),
                scopes=token_info.get("scope", "").split(),
                expires_at=token_info.get("exp")
            )
        except KeycloakError as e:
            logger.error(f"Keycloak token validation error: {e.response_body}")
            return None
        except Exception as e:
            logger.error(f"Access token validation failed: {str(e)}")
            return None

    async def revoke_token(
        self,
        token: KeycloakAccessToken | KeycloakRefreshToken
    ) -> None:
        """Revoke token with logging"""
        logger.info(f"Revoking token for client: {token.client_id}")
        
        try:
            if isinstance(token, KeycloakAccessToken):
                self.keycloak_openid.logout(token.token)
                logger.debug("Successfully revoked access token")
            else:
                self.keycloak_openid.logout(refresh_token=token.token)
                logger.debug("Successfully revoked refresh token")
        except KeycloakError as e:
            logger.error(f"Keycloak revocation error: {e.response_body}")
            raise TokenError("invalid_request", "Token revocation failed")
        except Exception as e:
            logger.error(f"Token revocation failed: {str(e)}")
            raise TokenError("server_error", str(e))

def construct_redirect_uri(redirect_uri_base: str, **params: str | None) -> str:
    """Helper function to construct redirect URIs with query parameters"""
    logger.debug(f"Constructing redirect URI with params: {params}")
    parsed_uri = urlparse(redirect_uri_base)
    query_params = [(k, v) for k, vs in parse_qs(parsed_uri.query) for v in vs]
    for k, v in params.items():
        if v is not None:
            query_params.append((k, v))

    constructed_uri = urlunparse(parsed_uri._replace(query=urlencode(query_params)))
    logger.debug(f"Constructed URI: {constructed_uri}")
    return constructed_uri
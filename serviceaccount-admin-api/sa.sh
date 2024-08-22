#!/bin/bash

KEYCLOAK_URL=http://0.0.0.0:8084
REALM=test
CLIENT_ID=admin-cli
USERNAME=admin
PASS=admin
TOKEN_PATH="/realms/master/protocol/openid-connect/token"


echo $KEYCLOAK_URL


RESULT=`curl -sk --data "grant_type=password&client_id=$CLIENT_ID&username=$USERNAME&password=$PASS" $KEYCLOAK_URL$TOKEN_PATH`
TOKEN=$(jq -r '.access_token' <<< $RESULT)
echo $TOKEN

CREATE=`curl -sk --data-raw '{
   "authorizationServicesEnabled": false,
   "clientId": "admin-sa",
   "description": "admin-sa",
   "name": "admin-sa",
   "secret":"admin-sa",
    "directAccessGrantsEnabled": false,
    "serviceAccountsEnabled": true,
    "publicClient": false,
    "protocol": "openid-connect"
}' --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/clients`
echo $CREATE

RE=`curl -sk --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=realm-management`
realmMgmtClientId=$(jq -r '.[].id' <<< $RE)
echo $realmMgmtClientId


ROLES=`curl -sk --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/clients/$realmMgmtClientId/roles`
echo $ROLES
manageUser=$(jq -c '.[] | select( .name | contains("manage-users")).id' <<< $ROLES)
echo $manageUser
manageClients=$(jq -c '.[] | select( .name | contains("manage-clients")).id' <<< $ROLES)
echo $manageClients
manageRealm=$(jq -c '.[] | select( .name | contains("manage-realm")).id' <<< $ROLES)
echo $manageRealm



KAS=`curl -sk --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/clients?clientId=admin-sa`
kasClientId=$(jq -r '.[].id' <<< $KAS)

SVC=`curl -sk --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/clients/$kasClientId/service-account-user`
svcUserId=$(jq -r '.id' <<< $SVC)
echo $svcUserId

FINAL=`curl -sk --data-raw '[{"id": '$manageUser',"name": "manage-users"},{"id": '$manageRealm',"name": "manage-realm"},{"id": '$manageClients',"name": "manage-clients"}]' --header "Content-Type: application/json" --header "Authorization: Bearer $TOKEN" $KEYCLOAK_URL/admin/realms/$REALM/users/$svcUserId/role-mappings/clients/$realmMgmtClientId`
echo $FINAL



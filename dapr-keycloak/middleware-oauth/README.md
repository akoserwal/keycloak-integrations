# Dapr integration with Keycloak

# Install Dapr in the Kubernetes cluster
`dapr init --kubernetes`

`dapr dashboard -k -p 9999`


# Settup Keycloak server and expose 
`./kc.sh start-dev --http-port=8081`


`http://localhost:8081`

Create a demo realm
Create a client: `dapr-demo`with root url retrived from the below command

```
docker container inspect kind-control-plane \
  --format '{{ .NetworkSettings.Networks.kind.IPAddress }
```

Create a user in Keycloak(demo realm)

Exposing Keycloak to public url
`ngrok http 8081`



# Update the Oauth2 configuration with Keycloak server url
[oauth.yaml](dapr-deploy/oauth2.yaml)


# Deploy goechoapp to with Dapr oauth2 configuration


`$ kubectl apply -f dapr-deploy/goechoapp.yaml`

`$ kubectl apply -f dapr-deploy/ingress.yaml`

`$ kubectl apply -f dapr-deploy/oauth2.yaml`

`$ kubectl apply -f dapr-deploy/pipeline.yaml`


If you are redeploying the oauth2 config

`kubectl rollout restart deployment/goechoapp`
`kubectl rollout restart deployment/dapr-operator -n dapr-system`

# Verify

`<kind-cluster-ip>/v1.0/invoke/goechoapp/method/echo?text=hello`



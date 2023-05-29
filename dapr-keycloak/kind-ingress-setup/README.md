# Kubernetes Kind cluster

Create a Kubernetes cluster
`kind create cluster --config kind-ingress.config`

# Ingress-nginx

ingress-nginx is an Ingress controller for Kubernetes using NGINX as a reverse proxy and load balancer.
* https://github.com/kubernetes/ingress-nginx
* Kind deployment: https://github.com/kubernetes/ingress-nginx/blob/main/deploy/static/provider/kind/deploy.yaml

## Deploy the ingress-nginx 

`kubectl apply -f deploy.yaml`

## Exposing localhost as service in the Kind cluster
https://github.com/kubernetes-sigs/kind/issues/1200

`kubectl apply -f localhost-expose-svc.yaml`




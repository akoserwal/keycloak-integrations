package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/golang-jwt/jwt/v4"
	"github.com/gorilla/mux"
	"github.com/lestrrat-go/jwx/jwk"
	"github.com/rs/cors"
	"log"
	"net/http"
	"strings"
	"time"
)
var count int
const jwksURL = `http://127.0.0.1:8081/realms/keycloak-demo/protocol/openid-connect/certs`
const host = "127.0.0.1"
const port = "8086"

func main() {
	c := cors.New(cors.Options{
		AllowedMethods: []string{
			http.MethodGet,//http methods for your app
			http.MethodPost,
		},
		AllowedHeaders: []string{
			"*",
		},
		AllowedOrigins: []string{"*"},
		AllowCredentials: true,
		// Enable Debugging for testing, consider disabling in production
		Debug: false,
	})

	//count service
	r := mux.NewRouter()
	r.HandleFunc("/count", CountHandler)
	fmt.Printf("Starting server at port %s \n", port)
	http.Handle("/", r)

	handler := c.Handler(r)
	srv := &http.Server{
		Handler: handler,
		Addr:    host + ":" + port,
		// Good practice: enforce timeouts for servers you create!
		WriteTimeout: 15 * time.Second,
		ReadTimeout:  15 * time.Second,
	}
	log.Fatal(srv.ListenAndServe())
}


func CountHandler(writer http.ResponseWriter, request *http.Request) {
	token, err := verifyToken(request)
	if err != nil {
		json.NewEncoder(writer).Encode(map[string]string{"reason": err.Error()})
		writer.WriteHeader(http.StatusBadRequest)
	}
	if token!= nil && token.Valid {
		count++
		writer.WriteHeader(http.StatusOK)
		json.NewEncoder(writer).Encode(map[string]int{"count": count})
	} else {
		writer.WriteHeader(http.StatusForbidden)
	}
}

func verifyToken(request *http.Request) (*jwt.Token, error ){
	strToken, err := GetAuthHeader(request)
	if err != nil {
		return nil, err
	}
	token, err := jwt.Parse(strToken, getKey)
	if err != nil {
		return nil, err
	}
	return token, nil
}

func GetAuthHeader(request *http.Request) (string, error) {
	header := strings.Fields (request.Header.Get("Authorization"))
	if header[0] != "Bearer" {
		return "", errors.New("malformed token")
	}
	return header[1], nil
}

func getKey(token *jwt.Token) (interface{}, error) {
	ctx := context.Background()
	keySet, err := jwk.Fetch(ctx, jwksURL)
	if err != nil {
		return nil, err
	}

	keyID, ok := token.Header["kid"].(string)
	if !ok {
		return nil, errors.New("expecting JWT header to have string kid")
	}

	keys, ok := keySet.LookupKeyID(keyID);
	if ok == false {
		return nil, fmt.Errorf("key %v not found", keyID)
	}

	var raw interface{}
	return raw, keys.Raw(&raw)
}


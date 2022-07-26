package client

import (
	"context"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"github.com/Nerzal/gocloak/v11"
	"kc-dyanmic-client/pkg/db"
)

type Client interface {
}

type kcClient struct {
	kcClient    gocloak.GoCloak
	ctx         context.Context
	realm       string
	intialToken string
	BaseUrl     string
}

var _ Client = &kcClient{}

func NewClient(baseUrl string, debug bool) *kcClient {
	client := gocloak.NewClient(baseUrl)
	client.RestyClient().SetDebug(debug)
	client.RestyClient().SetTLSClientConfig(&tls.Config{InsecureSkipVerify: true})
	return &kcClient{
		kcClient: client,
		ctx:      context.Background(),
		realm:    "demo",
		//Configure Intial Access Token
		intialToken: "",
		BaseUrl:     baseUrl,
	}
}

func (kc *kcClient) RegisterDynamicClient() (*string, error) {
	var url = fmt.Sprintf("%s/auth/realms/%s/clients-registrations/default", kc.BaseUrl, kc.realm)
	//const url = "http://127.0.0.1:8180/auth/realms/demo/clients-registrations/default"
	resp, err := kc.kcClient.RestyClient().R().
		SetAuthToken(kc.intialToken).
		SetBody(map[string]interface{}{
			"name":                   "test-client",
			"redirectUris":           []string{"https://127.0.0.1"},
			"consentRequired":        false,
			"standardFlowEnabled":    true,
			"implicitFlowEnabled":    false,
			"serviceAccountsEnabled": false,
			"publicClient":           true,
			"frontchannelLogout":     false,
			"fullScopeAllowed":       false,
		}).
		Post(url)
	if err != nil {
		fmt.Println(err)
	}
	fmt.Println(resp)
	var data gocloak.Client
	body := resp.Body()
	err = json.Unmarshal(body, &data)
	if err != nil {
		return nil, err
	}

	id := data.ID
	token := data.RegistrationAccessToken
	d := db.NewConnection()
	ss, err := d.SaveClients(*id, *token)
	if err != nil {
		return nil, err
	}
	fmt.Println(*ss)
	d.Close()
	return id, nil
}

func (kc *kcClient) DeleteClients(client_id string) error {
	d := db.NewConnection()
	id, token := d.GetClients(client_id)
	err := kc.kcClient.DeleteClientRepresentation(kc.ctx, token, kc.realm, id)
	if err != nil {
		return err
	}
	rows := d.Delete(id)
	fmt.Println(rows)
	d.Close()
	return nil
}

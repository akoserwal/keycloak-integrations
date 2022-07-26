package main

import (
	"fmt"
	"kc-dyanmic-client/pkg/client"
)

func main() {
	fmt.Println("ssadd")
	var kcClient = client.NewClient("http://127.0.0.1:8180", true)

	// Register client
	id, _ := kcClient.RegisterDynamicClient()
	if id != nil {
		fmt.Println(*id)
	}

	//Delete client
	//err := kcClient.DeleteClients(*id)
	//if err != nil {
	//	fmt.Println(err)
	//}

}

package io.akoserwa.SecureKeycloakApp;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecureKeycloakAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureKeycloakAppApplication.class, args);
	}

}

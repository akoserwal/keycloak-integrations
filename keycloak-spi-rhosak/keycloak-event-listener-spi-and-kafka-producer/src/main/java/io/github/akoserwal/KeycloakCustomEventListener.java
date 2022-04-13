package io.github.akoserwal;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class KeycloakCustomEventListener implements EventListenerProvider {

	@Override
	public void onEvent(Event event) {
		System.out.println("Event:-clientid" + event.getClientId());
		System.out.println("event-ip" + event.getIpAddress());
		System.out.println(event.getType());

		if (event.getType().name() == "CLIENT_LOGIN") {
			Map<String, String> clientmap = new HashMap<String, String>();
			clientmap.put("clientId", event.getClientId());
			clientmap.put("time", String.valueOf(event.getTime()));
			clientmap.put("ip", event.getIpAddress());
			clientmap.put("operation", event.getType().name());
			System.out.println(clientmap.toString());
			System.out.println("producer");
			Producer.publishEvent("CLIENT_LOGIN", clientmap.toString());
		}

	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
		System.out.println("Admin Event:-" + adminEvent.getResourceType().name());
		if (adminEvent.getResourceType().name() == "CLIENT" && adminEvent.getOperationType().toString() == "CREATE") {
			String clientId = null;
			HashMap<String, String> prodMap = new HashMap<>();

			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, Object>> typeRef
					= new TypeReference<
					HashMap<String, Object>
					>() {
			};
			try {
				HashMap<String, Object> clientmap
						= mapper.readValue(adminEvent.getRepresentation(), typeRef);
				clientId = (String) clientmap.get("clientId");
				prodMap.put("clientId", clientId.toString());
				prodMap.put("operation", adminEvent.getOperationType().name());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			Producer.publishEvent("CLIENT", prodMap.toString());
			System.out.println("produced");
		}

	}


	@Override
	public void close() {

	}
}

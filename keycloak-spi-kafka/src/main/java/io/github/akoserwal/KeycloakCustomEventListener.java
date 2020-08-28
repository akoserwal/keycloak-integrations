package io.github.akoserwal;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class KeycloakCustomEventListener implements EventListenerProvider {

	@Override
	public void onEvent(Event event) {
			System.out.println("Event:-"+event.getUserId());
			Producer.publishEvent(event.getType().toString(), event.getUserId());
	}

	@Override
	public void onEvent(AdminEvent adminEvent, boolean b) {
			System.out.println("Admin Event:-"+adminEvent.getResourceType().name());
			Producer.publishEvent(adminEvent.getOperationType().toString(), adminEvent.getAuthDetails().getUserId());
	}

	@Override
	public void close() {

	}
}

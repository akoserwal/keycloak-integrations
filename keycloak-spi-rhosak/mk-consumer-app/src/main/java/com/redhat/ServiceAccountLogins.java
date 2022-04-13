package com.redhat;

import io.smallrye.mutiny.Multi;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/service_accounts_logins")
@RequestScoped
public class ServiceAccountLogins {
	private final ServiceAccountRepository posts;

	@Inject
	public ServiceAccountLogins(ServiceAccountRepository posts) {
		this.posts = posts;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Multi<ServiceAccount> getAllServiceAccounts() {
		return this.posts.findlogins();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Multi<ServiceAccount> getServiceAccbyId(@PathParam("id") final String id) {
		return this.posts.findloginsById(id);
	}
}
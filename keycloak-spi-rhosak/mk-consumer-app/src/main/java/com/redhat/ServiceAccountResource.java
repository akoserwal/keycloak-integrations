package com.redhat;


import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.status;

@Path("/service_accounts")
@RequestScoped
public class ServiceAccountResource {
	private final static Logger LOGGER = Logger.getLogger(ServiceAccountResource.class.getName());
	private final ServiceAccountRepository accountRepository;

	@Inject
	public ServiceAccountResource(ServiceAccountRepository posts) {
		this.accountRepository = posts;
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Multi<ServiceAccount> getAllServiceAccounts() {
		return this.accountRepository.findAll();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> saveServiceAcc(@Valid ServiceAccount post) {
		return this.accountRepository.save(post)
				.map(id -> created(URI.create("/service_accounts/" + id)).build());
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getServiceAccountsById(@PathParam("id") final String id) {
		return this.accountRepository.findById(id)
				.map(data -> {
					if (data == null) {
						return null;
					}
					return ok(data).build();
				})
				//        .onItem().ifNull().continueWith(status(Status.NOT_FOUND).build());
				.onFailure(NotFoundException.class).recoverWithItem(status(Status.NOT_FOUND).build());
	}


	@DELETE
	@Path("{id}")
	public Uni<Response> delete(@PathParam("id") String id) {
		return this.accountRepository.delete(id)
				.map(deleted -> deleted > 0 ? Status.NO_CONTENT : Status.NOT_FOUND)
				.map(status -> status(status).build());
	}
}

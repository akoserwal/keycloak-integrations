package com.redhat;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.stream.StreamSupport;


@ApplicationScoped
public class ServiceAccountRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAccountRepository.class);

	private final PgPool client;

	@Inject
	public ServiceAccountRepository(PgPool client) {
		this.client = client;
	}

	public Multi<ServiceAccount> findAll() {
		return this.client
				.query("SELECT * FROM serviceaccounts")
				.execute()
				.onItem().produceMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
				.map(this::rowToServiceAcc);
	}

	public Multi<ServiceAccount> findlogins() {
		return this.client
				.query("SELECT * FROM serviceaccounts_logins")
				.execute()
				.onItem().produceMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
				.map(this::rowToLogins);
	}

	public Uni<ServiceAccount> findById(String id) {
		return this.client
				.preparedQuery("SELECT * FROM serviceaccounts WHERE client_id=$1")
				.execute(Tuple.of(id))
				.map(RowSet::iterator)
				// .map(it -> it.hasNext() ? rowToPost(it.next()) : null);
				.flatMap(it -> it.hasNext() ? Uni.createFrom().item(rowToServiceAcc(it.next())) : Uni.createFrom().failure(NotFoundException::new));
	}

	public Multi<ServiceAccount> findloginsById(String id) {
		return this.client
				.preparedQuery("SELECT * FROM serviceaccounts_logins WHERE client_id=$1")
				.execute(Tuple.of(id))
				.onItem().produceMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
				.map(this::rowToLogins);
	}

	private ServiceAccount rowToLogins(Row row) {
		return ServiceAccount.of(row.getString("client_id"), row.getLocalDateTime("login_timestamp"));
	}

	private ServiceAccount rowToServiceAcc(Row row) {
		return ServiceAccount.of(row.getString("client_id"), row.getString("operation"), row.getString("ip"), row.getInteger("count"), row.getString("status"), row.getLocalDateTime("created_at"), row.getLocalDateTime("last_update_at"));
	}

	public Uni<String> saveLogins(ServiceAccount data) {
		return this.client
				.preparedQuery("INSERT INTO serviceaccounts_logins (client_id, login_timestamp) VALUES ($1, $2) RETURNING (client_id)")
				.execute(Tuple.of(data.getClientId(), data.getLast_update_at()))
				.map(RowSet::iterator)
				.map(it -> it.hasNext() ? it.next().getString("client_id") : null);
	}

	public Uni<String> save(ServiceAccount data) {
		Uni<String> id = null;

		if (data != null) {
			if (data.getOperation().equals("CLIENT_LOGIN")) {
				System.out.println(data.getClientId());
				System.out.println(data.getOperation());
				System.out.println(data.getIp());
				System.out.println(data.getStatus());

				return id = this.client
						.preparedQuery("UPDATE serviceaccounts SET ip=$2,status=$3,last_update_at=$4,count=count+1,operation=$5 WHERE client_id=$1 RETURNING (client_id)")
						.execute(Tuple.of(data.getClientId(), data.getIp(), data.getStatus(), data.getLast_update_at(), data.getOperation()))
						.map(RowSet::iterator)
						.map(it -> it.hasNext() ? it.next().getString("client_id") : null);
			}

			if (data.getOperation().equals("CREATE")) {
				return id = this.client
						.preparedQuery("INSERT INTO serviceaccounts (client_id, operation, created_at) VALUES ($1, $2, $3) RETURNING (client_id)")
						.execute(Tuple.of(data.getClientId(), data.getOperation(), data.getCreated_at()))
						.map(RowSet::iterator)
						.map(it -> it.hasNext() ? it.next().getString("client_id") : null);
			}
		}
		return id;
	}

	public Uni<Integer> deleteAll() {
		return client.query("DELETE FROM serviceaccounts")
				.execute()
				.map(RowSet::rowCount);
	}

	public Uni<Integer> delete(String id) {
		return client.preparedQuery("DELETE FROM serviceaccounts WHERE client_id=$1")
				.execute(Tuple.of(id))
				.map(RowSet::rowCount);
	}
}

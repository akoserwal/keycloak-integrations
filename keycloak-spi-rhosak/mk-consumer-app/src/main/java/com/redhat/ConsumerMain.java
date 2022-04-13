package com.redhat;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@QuarkusMain
public class ConsumerMain implements QuarkusApplication {

	@ConfigProperty(name = "consumer.bootstrap.url")
	String bootstrapHostname;

	@ConfigProperty(name = "consumer.oauth.clientid")
	String oauth_clientid;

	@ConfigProperty(name = "consumer.oauth.secret")
	String oauth_secret;

	@ConfigProperty(name = "consumer.oauth.token.url")
	String oauth_token_url;

	@ConfigProperty(name = "consumer.group.id.config")
	String group_id_config;

	private final ServiceAccountRepository accountRepository;

	@Inject
	public ConsumerMain(ServiceAccountRepository posts) {
		this.accountRepository = posts;
	}

	@Override
	public int run(String... args) throws Exception {
		Properties p = getProperties();
		KafkaConsumer consumer = new KafkaConsumer(p);
		String client_login = "CLIENT_LOGIN";
		String client = "CLIENT";
		consumer.subscribe(Arrays.asList(client, client_login));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				System.out.println(" Value:" + record.value());
				String c = record.value().toString();
				c = c.substring(1, c.length() - 1);
				String[] RecordPairs = c.split(",");
				Map<String, String> clientmap = new HashMap<>();

				for (String pair : RecordPairs) {
					String[] entry = pair.split("=");
					clientmap.put(entry[0].trim(), entry[1].trim());
				}

				String operation = clientmap.get("operation");
				String clientId = clientmap.get("clientId");
				ServiceAccount sa = new ServiceAccount(clientId);
				sa.setOperation(operation);

				if (operation.equals( "CREATE")) {
					//insert new db entry
					System.out.println("create");
					Instant instant = Instant.now();
					LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
					sa.setCreated_at(ldt);

					accountRepository.save(sa).map(id -> id.toString()).subscribe().with(
							item -> System.out.println(item),
							failure -> System.out.println("Failed with " + failure));

				}

				if (operation.equals("CLIENT_LOGIN")) {
					System.out.println("login");
					String ip = clientmap.get("ip");
					String time = clientmap.get("time");
					sa.setClientId(clientId);
					sa.setOperation(operation);
					sa.setIp(ip);
					Instant instant = Instant.ofEpochSecond(Long.parseLong(time));
					LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
					sa.setLast_update_at(ldt);
					sa.setStatus("active");

					accountRepository.save(sa).map(id -> id.toString()).subscribe().with(
							item -> System.out.println(item),
							failure -> System.out.println("Failed with " + failure));
					accountRepository.saveLogins(sa).map(id -> id.toString()).subscribe().with(
							item -> System.out.println(item+"entery"),
							failure -> System.out.println("Failed with timelogs" + failure));
				}

				System.out.println(sa.toString());

			}
		}

	}

	private Properties getProperties() {
		Properties p = new Properties();
		p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapHostname);
		p.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		p.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group_id_config);
		p.setProperty("security.protocol", "SASL_SSL");
		p.setProperty("sasl.mechanism", "OAUTHBEARER");
		p.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
		p.setProperty("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
		Properties defaults = new Properties();
		p.setProperty("ssl.protocol","TLSv1.3");
		defaults.setProperty(ClientConfig.OAUTH_TOKEN_ENDPOINT_URI, oauth_token_url);
		defaults.setProperty(ClientConfig.OAUTH_CLIENT_ID, oauth_clientid);
		defaults.setProperty(ClientConfig.OAUTH_CLIENT_SECRET, oauth_secret);
		defaults.setProperty(ClientConfig.OAUTH_USERNAME_CLAIM, "preferred_username");
		ConfigProperties.resolveAndExportToSystemProperties(defaults);
		return p;
	}
}

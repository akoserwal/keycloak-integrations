package io.akoserwal;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.security.sasl.AuthenticationException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@QuarkusMain
public class ProducerApp implements QuarkusApplication {

	@ConfigProperty(name = "keycloak.ca.pass")
	public String KEYCLOAK_TRUST_PASSWORD;

	@ConfigProperty(name = "kafka.ca.pass")
	public String KAFKA_TRUST_PASSWORD;

	@ConfigProperty(name = "keycloak.user.claim")
	public static final String PREFERRED_USERNAME = "preferred_username";

	@ConfigProperty(name = "keycloak.host")
	String keycloak_host;

	@ConfigProperty(name = "keycloak.realm")
	String realm;

	@ConfigProperty(name = "keycloak.clientid")
	String clientId;

	@ConfigProperty(name = "keycloak.secret")
	String secret;

	@ConfigProperty(name = "kafka.host")
	String bootstrap_url;

	@ConfigProperty(name = "kafka.ca")
	String CA_CERT_PATH;

	@ConfigProperty(name = "keycloak.ca")
	String keycloak_ssl_truststore;

	@ConfigProperty(name = "kafka.topic")
	String topic;

	@Override
	public int run(String... args) throws Exception {

		System.out.println(keycloak_host);
		System.out.println(bootstrap_url);

		Properties p = new Properties();
		p.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_url);
		p.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		p.setProperty("security.protocol", "SASL_SSL");
		p.setProperty(ProducerConfig.ACKS_CONFIG,"all");
		p.setProperty("sasl.mechanism", "OAUTHBEARER");
		p.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
		p.setProperty("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
		p.setProperty("ssl.truststore.location", CA_CERT_PATH);
		p.setProperty("ssl.keystore.type", "jks");
		p.setProperty("ssl.truststore.password", KAFKA_TRUST_PASSWORD);

		Properties defaults = new Properties();
		defaults.setProperty(ClientConfig.OAUTH_TOKEN_ENDPOINT_URI, keycloak_host+"/auth/realms/"+realm+"/protocol/openid-connect/token");
		defaults.setProperty(ClientConfig.OAUTH_CLIENT_ID, clientId);
		defaults.setProperty(ClientConfig.OAUTH_CLIENT_SECRET, secret);
		defaults.setProperty(ClientConfig.OAUTH_USERNAME_CLAIM, PREFERRED_USERNAME);
		defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_PASSWORD, KEYCLOAK_TRUST_PASSWORD);
		defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_LOCATION, keycloak_ssl_truststore);
		defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_TYPE, "jks");
		ConfigProperties.resolveAndExportToSystemProperties(defaults);

		org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<>(p);

		for (int i = 0; ; i++) {
			try {
				producer.send(new ProducerRecord<>(topic, "Message " + i))
						.get();

				System.out.println("Produced Message " + i);

			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while sending!");

			} catch (ExecutionException e) {
				if (e.getCause() instanceof AuthenticationException
						|| e.getCause() instanceof AuthorizationException) {
					producer.close();
					producer = new KafkaProducer<>(p);
				} else {
					throw new RuntimeException("Failed to send message: " + i, e);
				}
			}

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted while sleeping!");
			}
		}

	}
}

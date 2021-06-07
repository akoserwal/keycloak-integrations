package io.akoserwal;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

@QuarkusMain
public class ConsumerApp implements QuarkusApplication {

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
		p.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_url);
		p.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		p.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
		//p.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
		p.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "a_consumer-group");
		p.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
		p.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		p.setProperty("security.protocol", "SASL_SSL");
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
		KafkaConsumer consumer = new KafkaConsumer(p);

		consumer.subscribe(Arrays.asList(topic));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
			for (ConsumerRecord<String, String> record : records) {
				System.out.println("Key: " + record.key() + ", Value:" + record.value());
			}
		}
	}
}

package io.github.akoserwal;


import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Future;


public class Producer {
	public static void publishEvent(String topic, String value) {
		Properties properties = new Properties();
		try {
			properties.load(Producer.class.getClassLoader().getResourceAsStream("/META-INF/application.properties"));
		} catch (
	IOException e) {
			e.printStackTrace();
		}
		String BOOTSTRAP_SERVER = properties.getProperty("bootstrap");
		System.out.println(properties.getProperty("OAUTH_CLIENT_ID"));
		System.out.println(properties.getProperty("OAUTH_CLIENT_ID"));
		properties.setProperty("security.protocol", "SASL_SSL");
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		properties.setProperty("sasl.mechanism", "OAUTHBEARER");
		properties.setProperty("ssl.protocol", "TLSv1.3");
		properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
		properties.setProperty("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ClientConfig.OAUTH_TOKEN_ENDPOINT_URI, properties.getProperty("OAUTH_TOKEN_ENDPOINT_URI"));
		properties.setProperty(ClientConfig.OAUTH_CLIENT_ID, properties.getProperty("OAUTH_CLIENT_ID"));
		properties.setProperty(ClientConfig.OAUTH_CLIENT_SECRET, properties.getProperty("OAUTH_CLIENT_SECRET"));
		properties.setProperty(ClientConfig.OAUTH_USERNAME_CLAIM, "preferred_username");

		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		ConfigProperties.resolveAndExportToSystemProperties(properties);

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
		ProducerRecord<String, String> eventRecord =
				new ProducerRecord<String, String>(topic, value);

		// send data - asynchronous
		Future<RecordMetadata> res = producer.send(eventRecord);
		System.out.println("producer called");
		// flush data
		producer.flush();
		// flush and close producer
		producer.close();
	}
}


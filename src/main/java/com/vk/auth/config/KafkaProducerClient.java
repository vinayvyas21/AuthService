package com.vk.auth.config;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerClient {
	// This is a Producer for Kafka.
	// Whenever we need to add an event to Kafka, we'll use this Producer.
	private KafkaTemplate<String, String> kafkaTemplate;

	public KafkaProducerClient(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendMessage(String topic, String message) {
		kafkaTemplate.send(topic, message);
	}
}
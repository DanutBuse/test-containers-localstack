package com.danutbuse.testcontainers.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaProducer {

  @Autowired
  KafkaTemplate<String, KafkaTestModel> kafkaTemplate;

  @Value("${spring.kafka.topic.name}")
  String topic;

  public void produce(String message) {
    kafkaTemplate.send(
        new ProducerRecord<>(
            topic,
            UUID.randomUUID().toString(),
            new KafkaTestModel(UUID.randomUUID().toString(), message)
        )
    );
    kafkaTemplate.flush();
  }
}

package com.danutbuse.testcontainers.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaProducer {

  @Autowired
  KafkaTemplate<String, Pet> kafkaTemplate;

  @Value("${spring.kafka.topic.name}")
  String topic;

  public void produce(Pet pet) {
    kafkaTemplate.send(
        topic,
        UUID.randomUUID().toString(),
        pet
    );
  }
}

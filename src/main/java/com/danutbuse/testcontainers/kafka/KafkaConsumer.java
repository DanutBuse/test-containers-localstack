package com.danutbuse.testcontainers.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
public class KafkaConsumer {

  @KafkaHandler(isDefault = true)
  public void consume(ConsumerRecord<String, KafkaTestModel> consumerRecord) {
    System.out.println("Received message: " + consumerRecord.value());
  }
}

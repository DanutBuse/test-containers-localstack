package com.danutbuse.testcontainers.sqs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SqsProducer {

  @Value("${aws.sqs.queue}")
  private String queueName;

  @Autowired
  private QueueMessagingTemplate messagingTemplate;

  public void sendToFifoQueue(
      final String messagePayload
  ) {
    messagingTemplate.convertAndSend(
        queueName,
        MessageBuilder.withPayload(messagePayload).build()
    );
  }
}

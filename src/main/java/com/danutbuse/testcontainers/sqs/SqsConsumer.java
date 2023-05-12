package com.danutbuse.testcontainers.sqs;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class SqsConsumer {

  @SqsListener(value = "${aws.sqs.queue}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void processMessage(String message) {
    System.out.println("Message: " + message);
  }

}

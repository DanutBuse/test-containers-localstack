package com.danutbuse.testcontainers.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Configuration
public class SqsConfiguration {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.accessKeyId}")
  private String accessKey;

  @Value("${aws.secretKey}")
  private String secretKey;

  @Value("${aws.sqs.endpoint}")
  private String endpoint;

  @Bean
  public AmazonSQSAsync buildAmazonSQSAsync() {

    return AmazonSQSAsyncClientBuilder
        .standard()
        .withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
        .withCredentials(
            new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
            )
        )
        .build();

  }

  @Bean
  public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    return new QueueMessagingTemplate(amazonSQSAsync);
  }

  @Bean
  public QueueMessageHandler queueMessageHandler(AmazonSQSAsync amazonSQSAsync) {
    QueueMessageHandlerFactory queueMessageHandlerFactory = new QueueMessageHandlerFactory();
    queueMessageHandlerFactory.setAmazonSqs(amazonSQSAsync);
    QueueMessageHandler queueMessageHandler = queueMessageHandlerFactory.createQueueMessageHandler();
    queueMessageHandler.setArgumentResolvers(
        List.of(new PayloadMethodArgumentResolver(new StringMessageConverter()))
    );
    return queueMessageHandler;
  }

  @Bean
  public SimpleMessageListenerContainer simpleMessageListenerContainer(
      AmazonSQSAsync amazonSQSAsync,
      QueueMessageHandler queueMessageHandler
  ) {

    SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
    simpleMessageListenerContainer.setAmazonSqs(amazonSQSAsync);
    simpleMessageListenerContainer.setMessageHandler(queueMessageHandler);
    simpleMessageListenerContainer.setMaxNumberOfMessages(10);
    simpleMessageListenerContainer.setTaskExecutor(threadPoolTaskExecutor());
    return simpleMessageListenerContainer;
  }

  private ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(10);
    executor.initialize();
    return executor;
  }
}

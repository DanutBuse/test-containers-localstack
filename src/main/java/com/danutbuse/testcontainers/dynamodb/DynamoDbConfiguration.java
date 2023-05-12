package com.danutbuse.testcontainers.dynamodb;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = CustomerRepository.class)
public class DynamoDbConfiguration {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.accessKeyId}")
  private String accessKey;

  @Value("${aws.secretKey}")
  private String secretKey;

  @Value("${aws.dynamodb.endpoint}")
  private String endpoint;

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
        .build();

    return amazonDynamoDB;
  }

}

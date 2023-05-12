package com.danutbuse.testcontainers.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.accessKeyId}")
  private String accessKey;

  @Value("${aws.secretKey}")
  private String secretKey;

  @Value("${aws.s3.endpoint}")
  private String endpoint;

  @Bean
  public AmazonS3 amazonS3() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
        .build();
  }

}

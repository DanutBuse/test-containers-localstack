package com.danutbuse.testcontainers.s3;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(SpringExtension.class)
@Import({S3Configuration.class})
public class S3BaseTest {

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:1.1.0")
  )
      .withServices(Service.S3);

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "aws.s3.endpoint",
        () -> localStack.getEndpointOverride(Service.S3).toString()
    );
    registry.add(
        "aws.accessKeyId",
        () -> localStack.getAccessKey()
    );		registry.add(
        "aws.secretKey",
        () -> localStack.getSecretKey()
    );
    registry.add(
        "aws.region",
        localStack::getRegion
    );
  }
}

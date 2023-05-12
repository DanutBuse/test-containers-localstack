package com.danutbuse.testcontainers.sqs;

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
@Import({SqsConfiguration.class})
public class SqsBaseTest {

  @Container
  static LocalStackContainer localStack = new LocalStackContainer(
      DockerImageName.parse("localstack/localstack:1.1.0")
  )
      .withServices(Service.SQS);

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "aws.sqs.endpoint",
        () -> localStack.getEndpointOverride(Service.SQS).toString()
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

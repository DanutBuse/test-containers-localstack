package com.danutbuse.testcontainers.sqs;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

@Import({SqsProducer.class, SqsConsumer.class})
@TestPropertySource(properties = "aws.sqs.queue=queueName")
class SqsServiceIntegrationTest extends SqsBaseTest {

	@Autowired
	SqsProducer sqsProducer;

	@SpyBean
	SqsConsumer sqsConsumer;

	@BeforeAll
	static void createQueue() throws IOException, InterruptedException {
		localStack.execInContainer(
				"awslocal", "sqs", "create-queue", "--queue-name", "queueName"
		);
	}

	@Test
	void shouldTestSqsIntegration() {
		sqsProducer.sendToFifoQueue("TEST");
		verify(sqsConsumer, timeout(5000)).processMessage(contains("TEST"));
	}

}

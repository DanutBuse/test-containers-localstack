package com.danutbuse.testcontainers.dynamodb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;

class DynamoDbIntegrationTest extends DynamoDbBaseTest {

	@Autowired
	CustomerRepository customerRepository;

	@BeforeAll
	static void createTable() throws IOException, InterruptedException {
		localStack.execInContainer(
				"awslocal", "dynamodb", "create-table",
						"--table-name", "Customer",
						"--attribute-definitions", "AttributeName=CustomerID,AttributeType=S",
						"--key-schema", "AttributeName=CustomerID,KeyType=HASH",
						"--billing-mode", "PAY_PER_REQUEST",
						"--region", localStack.getRegion()
		);
	}

	@Test
	void shouldTestDynamoDbIntegration() {
		String customerId = UUID.randomUUID().toString();
		var customer = new CustomerEntity()
				.setCustomerID(customerId)
				.setName("name")
				.setEmail("email");

		customerRepository.save(customer);

		var actualCustomer = customerRepository.findById(customerId);
		assertThat(actualCustomer).isPresent().hasValue(customer);

		customerRepository.delete(customer);
		var deletedCustomer = customerRepository.findById(customerId);
		assertThat(deletedCustomer).isNotPresent();
	}

}

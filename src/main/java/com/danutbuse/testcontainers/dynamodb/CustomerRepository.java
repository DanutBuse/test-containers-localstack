package com.danutbuse.testcontainers.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface CustomerRepository extends CrudRepository<CustomerEntity, String> {}

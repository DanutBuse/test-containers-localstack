package com.danutbuse.testcontainers.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@DynamoDBTable(tableName = "Customer")
@Accessors(chain = true)
public class CustomerEntity {

  @DynamoDBHashKey(attributeName = "CustomerID")
  String customerID;

  @DynamoDBAttribute(attributeName = "Name")
  String name;

  @DynamoDBAttribute(attributeName = "Email")
  String email;
}

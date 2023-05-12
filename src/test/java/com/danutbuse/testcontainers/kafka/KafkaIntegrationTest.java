package com.danutbuse.testcontainers.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;

@Testcontainers
@ExtendWith(SpringExtension.class)
@Import({
    KafkaConsumerConfiguration.class,
    KafkaConsumer.class,
    KafkaProducer.class,
    KafkaProperties.class,
    KafkaProducerConfiguration.class
})
@TestPropertySource(
    properties = "spring.kafka.topic.name=mytopic"
)
class KafkaIntegrationTest {

  static Network network = Network.newNetwork();

  @Container
  static GenericContainer<?> zookeeperContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper:4.0.0"))
      .withLabel("name", "zookeeper")
      .withNetworkAliases("zookeeper")
      .withNetwork(network)
      .withEnv("ZOOKEEPER_CLIENT_PORT", "2181");

  @Container
  static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
      .withLabel("name", "kafka")
      .withExternalZookeeper("zookeeper:2181")
      .withExposedPorts(9092, KafkaContainer.KAFKA_PORT)
      .withNetworkAliases("kafka")
      .withNetwork(network)
      .dependsOn(zookeeperContainer);

  @Container
  static GenericContainer<?> schemaRegistryContainer = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:5.5.1"))
      .withLabel("name", "schema-registry")
      .withNetworkAliases("schema-registry")
      .withNetwork(network)
      .withEnv(
          Map.of(
              "SCHEMA_REGISTRY_HOST_NAME", "schema-registry",
              "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:9092"
          )
      )
      .withExposedPorts(8081)
      .dependsOn(kafkaContainer, zookeeperContainer);

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    registry.add("spring.kafka.properties.schema-registry.url",
        () -> "http://" + schemaRegistryContainer.getHost() + ":" + schemaRegistryContainer.getFirstMappedPort());
  }

  @BeforeAll
  static void createTopic() throws IOException, InterruptedException {
    kafkaContainer.execInContainer(
      "kafka-topics --create"
        + " --zookeeper localhost:2181"
        + " --replication-factor 1 --partitions 1"
        + " --topic mytopic"
    );
  }

  @Autowired
  private KafkaProducer kafkaProducer;

  @SpyBean
  private KafkaConsumer kafkaConsumer;

  @Test
  void testProduceAndConsumeKafkaMessage() {
    kafkaProducer.produce("TEST");
    verify(kafkaConsumer, timeout(15000)).consume(any());
  }

  @Test
  void testProduceInvalidKafkaMessage() {
    kafkaProducer.produce("asdfasfsafsafasfas");
    verify(kafkaConsumer, timeout(15000).times(0)).consume(any());
  }
}

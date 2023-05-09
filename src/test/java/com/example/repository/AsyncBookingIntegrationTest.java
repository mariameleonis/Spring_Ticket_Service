package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.config.RabbitMQConfig;
import com.example.rest.model.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@Testcontainers
@ContextConfiguration(classes = RabbitMQConfig.class)
class AsyncBookingIntegrationTest {

  private static final String EXCHANGE_NAME = "booking-exchange";
  private static final String QUEUE_NAME = "booking-queue";
  private static final String ROUTING_KEY = "my-routing-key";

  @Container
  private static final RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer("rabbitmq:3.9.9-management");

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ConnectionFactory connectionFactory;

  private Channel channel;

  @BeforeEach
  void setUp() throws IOException {
    channel = connectionFactory.createConnection().createChannel(false);
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
  }

  @AfterEach
  void tearDown() throws IOException, TimeoutException {
    channel.close();
    RABBIT_MQ_CONTAINER.stop();
  }

  @Test
  void testBookingConsumed() throws IOException {
    val booking = new Booking(1L, 123L, 6);

    rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, booking);

    val response = channel.basicGet(QUEUE_NAME, true);
    val consumedBooking = new ObjectMapper().readValue(response.getBody(), Booking.class);

    assertEquals(booking, consumedBooking);
  }

  @DynamicPropertySource
  static void setDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
    registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getFirstMappedPort);
    registry.add("spring.rabbitmq.username", RABBIT_MQ_CONTAINER::getAdminUsername);
    registry.add("spring.rabbitmq.password", RABBIT_MQ_CONTAINER::getAdminPassword);
  }

}

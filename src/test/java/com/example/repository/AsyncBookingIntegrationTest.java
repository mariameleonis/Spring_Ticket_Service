package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.config.RabbitMQTestConfiguration;
import com.example.rest.model.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RabbitMQTestConfiguration.class)
class AsyncBookingIntegrationTest {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ConnectionFactory connectionFactory;

  @Test
  void testBooking() throws IOException, TimeoutException {

    Channel channel = connectionFactory.createConnection().createChannel(false);
    String queueName = "booking-queue";
    String exchangeName = "booking-exchange";
    String routingKey = "my-routing-key";
    channel.queueDeclare(queueName, true, false, false, null);
    channel.exchangeDeclare(exchangeName, "direct");

    channel.queueBind(queueName, exchangeName, routingKey);

    Booking booking = new Booking(1L, 123L, 6);


    rabbitTemplate.convertAndSend(exchangeName, routingKey, booking);



    GetResponse response = channel.basicGet(queueName, true);
    Booking consumedBooking = new ObjectMapper().readValue(response.getBody(), Booking.class);

    assertNotNull(consumedBooking);
    assertEquals(1L, consumedBooking.getUserId());
    assertEquals(123L, consumedBooking.getEventId());
    assertEquals(6, consumedBooking.getPlace());

    channel.getConnection().close();
    channel.close();
  }

}

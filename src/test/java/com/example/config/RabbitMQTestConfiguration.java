package com.example.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
public class RabbitMQTestConfiguration {

  @Container
  public static final RabbitMQContainer RABBIT_MQ_CONTAINER = new RabbitMQContainer("rabbitmq:3.9.9-management");

  @Bean
  public ConnectionFactory connectionFactory() {
    RABBIT_MQ_CONTAINER.start();
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(RABBIT_MQ_CONTAINER.getHost());
    connectionFactory.setPort(RABBIT_MQ_CONTAINER.getFirstMappedPort());
    connectionFactory.setUsername(RABBIT_MQ_CONTAINER.getAdminUsername());
    connectionFactory.setPassword(RABBIT_MQ_CONTAINER.getAdminPassword());
    return connectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

}

package com.example.config;

import lombok.val;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

  @Value("${spring.rabbitmq.host}")
  private String rabbitMqHost;

  @Value("${spring.rabbitmq.port}")
  private int rabbitMqPort;

  @Value("${spring.rabbitmq.username}")
  private String rabbitMqUsername;

  @Value("${spring.rabbitmq.password}")
  private String rabbitMqPassword;

  @Value("${spring.rabbitmq.exchange}")
  private String rabbitMqExchange;

  @Value("${spring.rabbitmq.routing_key}")
  private String rabbitMqRoutingKey;

  @Value("${spring.rabbitmq.queue}")
  private String rabbitMqQueue;

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(rabbitMqHost);
    connectionFactory.setPort(rabbitMqPort);
    connectionFactory.setUsername(rabbitMqUsername);
    connectionFactory.setPassword(rabbitMqPassword);
    return connectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    val rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public Queue queue() {
    return new Queue(rabbitMqQueue);
  }

  @Bean
  public DirectExchange exchange() {
    return new DirectExchange(rabbitMqExchange);
  }

  @Bean
  public Binding binding(Queue queue, DirectExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(rabbitMqRoutingKey);
  }
}

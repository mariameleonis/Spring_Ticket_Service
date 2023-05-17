package com.example.config;

import com.example.messaging.BookingMessageListener;
import com.example.rest.model.Booking;
import java.util.Collections;
import lombok.val;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
public class RabbitMQTestConfiguration {

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
  public Jackson2JsonMessageConverter messageConverter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

    DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
    typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);

    typeMapper.setIdClassMapping(Collections.singletonMap("booking", Booking.class));

    converter.setJavaTypeMapper(typeMapper);

    return converter;
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

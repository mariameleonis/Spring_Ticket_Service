package com.example.messaging;

import com.example.rest.model.Booking;
import com.example.service.exception.BusinessException;
import com.example.service.facade.BookingFacade;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingMessageListener implements MessageListener {

  private final BookingFacade bookingFacade;
  private final Jackson2JsonMessageConverter messageConverter;

  @Override
  public void onMessage(Message message) {
    log.info("Start receiving message");

    val booking = (Booking) messageConverter.fromMessage(message);

    log.info("Booking message received " + booking);

    try {
      bookingFacade.bookTicket(booking.getUserId(), booking.getEventId(), booking.getPlace());
    } catch (BusinessException ex) {
      log.error("Failed to process message: " + ex.getMessage(), ex);
    }
  }
}

package com.example.messaging;

import com.example.rest.model.Booking;
import com.example.service.exception.BusinessException;
import com.example.service.facade.BookingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingMessageListener implements MessageListener {

  private final BookingFacade bookingFacade;
  private final RabbitTemplate rabbitTemplate;

  public void onMessage(Message message) {

    val booking = (Booking) rabbitTemplate.getMessageConverter().fromMessage(message);


    try {
      bookingFacade.bookTicket(booking.getUserId(), booking.getEventId(), booking.getPlace());

      rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
          message.getMessageProperties().getReceivedRoutingKey(),
          rabbitTemplate.getMessageConverter().toMessage("Acknowledged", new MessageProperties()));

    } catch (BusinessException ex) {
      log.error("Failed to process message: " + ex.getMessage(), ex);
    }
  }
}

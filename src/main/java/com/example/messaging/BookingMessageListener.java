package com.example.messaging;

import com.example.rest.model.Booking;
import com.example.service.exception.BusinessException;
import com.example.service.facade.BookingFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    // convert the message body to a Booking object
    Booking booking = (Booking) rabbitTemplate.getMessageConverter().fromMessage(message);

    // call the bookingFacade to book the ticket
    try {
      bookingFacade.bookTicket(booking.getUserId(), booking.getEventId(), booking.getPlace());

      // acknowledge the message to remove it from the queue
      rabbitTemplate.send(message.getMessageProperties().getReceivedExchange(),
          message.getMessageProperties().getReceivedRoutingKey(),
          rabbitTemplate.getMessageConverter().toMessage("Acknowledged", new MessageProperties()));

    } catch (BusinessException ex) {
      log.error("Failed to process message: " + ex.getMessage(), ex);
    }
  }
}

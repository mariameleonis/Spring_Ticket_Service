package com.example.rest.controller;

import com.example.entity.Ticket;
import com.example.rest.model.Booking;
import com.example.service.exception.BusinessException;
import com.example.service.exception.EventNotFoundException;
import com.example.service.facade.BookingFacade;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
  @Value("${spring.rabbitmq.exchange}")
  private String bookingExchange;

  @Value("${spring.rabbitmq.routing_key}")
  private String routingKey;

  public static final String BOOKING_REQUEST_SENT = "Booking request sent";
  public static final String TICKET_CANCELLED = "Ticket with ID %d cancelled successfully";
  private final BookingFacade bookingFacade;
  private final RabbitTemplate rabbitTemplate;

  @PostMapping
  public ResponseEntity<String> bookTicket(@RequestBody Booking booking) {
    rabbitTemplate.convertAndSend(bookingExchange, routingKey, booking);
    return ResponseEntity.ok(BOOKING_REQUEST_SENT);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Ticket>> getTicketsByUser(
      @PathVariable Long userId,
      @RequestParam(required = false, defaultValue = "10") int pageSize,
      @RequestParam(required = false, defaultValue = "0") int pageNum)
      throws BusinessException {

    val user = bookingFacade.getUserById(userId);

    val tickets = bookingFacade.getBookedTickets(user, pageSize, pageNum);

    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/event/{eventId}")
  public ResponseEntity<List<Ticket>> getTicketsByEvent(
      @PathVariable Long eventId,
      @RequestParam(required = false, defaultValue = "10") int pageSize,
      @RequestParam(required = false, defaultValue = "0") int pageNum)
      throws EventNotFoundException {

    val event = bookingFacade.getEventById(eventId);

    val tickets = bookingFacade.getBookedTickets(event, pageSize, pageNum);

    return ResponseEntity.ok(tickets);
  }

  @DeleteMapping("/{ticketId}")
  public ResponseEntity<String> cancelTicket(@PathVariable Long ticketId)
      throws BusinessException {

    bookingFacade.cancelTicket(ticketId);

    return ResponseEntity.ok(String.format(TICKET_CANCELLED, ticketId));
  }
}

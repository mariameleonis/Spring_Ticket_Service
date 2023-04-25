package com.example.rest.controller;

import com.example.entity.Ticket;
import com.example.rest.model.Booking;
import com.example.service.exception.BusinessException;
import com.example.service.facade.BookingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
  private final BookingFacade bookingFacade;

  @PostMapping
  public ResponseEntity<Ticket> bookTicket(@RequestBody Booking booking) throws BusinessException {
//    jmsTemplate.convertAndSend("bookingQueue", booking);
//    return "Booking request sent";

    return ResponseEntity.ok(
        bookingFacade.bookTicket(booking.getUserId(), booking.getEventId(), booking.getPlace()));
  }

  @GetMapping
  public String getTicket() {
    return "Get ticket";
  }

}

package com.example.application.service.facade.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.application.config.ApplicationConfig;
import com.example.application.model.Event;
import com.example.application.model.User;
import com.example.application.service.facade.BookingFacade;
import java.time.LocalDate;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class BookingFacadeImplIntegrationTest {

  private AnnotationConfigApplicationContext context;

  private BookingFacade bookingFacade;

  @BeforeEach
  void setup() {
    context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
    bookingFacade = context.getBean(BookingFacade.class);
  }


  @Test
  @DisplayName("Booking Flow")
  void testBookingFlow() {

    var user = User.builder().name("John Doe").email("johndoe@example.com").build();

    user = bookingFacade.createUser(user);

    assertNotEquals(0, user.getId());

    var event = Event.builder()
        .title("Spring Conference")
        .date(LocalDate.of(2023, 4, 20))
        .build();

    event = bookingFacade.createEvent(event);

    assertNotEquals(0, event.getId());

    val ticket = bookingFacade.bookTicket(user.getId(), event.getId(), 2);
    assertNotEquals(0, ticket.getId());

    val canceled = bookingFacade.cancelTicket(ticket.getId());

    assertTrue(canceled);

    bookingFacade.bookTicket(user.getId(), event.getId(), 3);

    bookingFacade.deleteUser(user.getId());

    val userTickets = bookingFacade.getBookedTickets(user, 2, 1);

    assertEquals(0, userTickets.size());
  }
}
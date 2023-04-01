package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.example.entity.Event;
import com.example.entity.User;
import com.example.service.exception.BusinessException;
import com.example.service.facade.BookingFacade;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookingFlowIntegrationTest extends AbstractRepositoryTest {

  @Autowired
  private BookingFacade bookingFacade;

  @Test
  @DisplayName("Booking Flow")
  void testBookingFlow() throws BusinessException {

    var user = User.builder().name("Sheldon Cooper").email("sheldon@example.com").build();

    val createdUser = bookingFacade.createUser(user);

    val userId = createdUser.getId();

    assertNotEquals(0, createdUser.getId());

    bookingFacade.refillUserAccountByUserId(userId, BigDecimal.valueOf(1000));

    var userAccount = bookingFacade.findUserAccountByUserId(userId);

    assertEquals(BigDecimal.valueOf(1000), userAccount.getMoney());

    var event = Event.builder()
        .title("Spring Conference")
        .date(LocalDate.of(2023, 4, 20))
        .ticketPrice(BigDecimal.valueOf(25))
        .build();

    val createdEvent = bookingFacade.createEvent(event);

    val eventId = createdEvent.getId();

    assertNotEquals(0, eventId);

    val ticket = bookingFacade.bookTicket(userId, eventId, 2);

    val ticketId = ticket.getId();

    assertNotEquals(0, ticketId);

    userAccount = bookingFacade.findUserAccountByUserId(userId);

    assertEquals(BigDecimal.valueOf(975), userAccount.getMoney());

    bookingFacade.cancelTicket(ticketId);

    val userTickets = bookingFacade.getBookedTickets(createdUser, 1, 0);

    assertEquals(0, userTickets.size());

    userAccount = bookingFacade.findUserAccountByUserId(userId);

    assertEquals(BigDecimal.valueOf(1000), userAccount.getMoney());
  }

}

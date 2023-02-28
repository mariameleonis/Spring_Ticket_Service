package com.example.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketValidatorTest {

  @Mock
  private TicketDao ticketDao;

  @InjectMocks
  private TicketValidator validator;

  @Test
  void testValidateWithUnbookedPlace() {
    int place = 1;
    val ticket = Ticket.builder().place(place).build();

    when(ticketDao.isPlaceBooked(place)).thenReturn(false);

    assertDoesNotThrow(() -> validator.validate(ticket));
  }

  @Test
  void testValidateWithBookedPlace() {
    int place = 2;
    val ticket = Ticket.builder().place(place).build();

    when(ticketDao.isPlaceBooked(place)).thenReturn(true);

    assertThrows(IllegalStateException.class, () -> validator.validate(ticket));
  }
}
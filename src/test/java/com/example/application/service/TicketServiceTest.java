package com.example.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

  @Mock
  private TicketDao ticketDao;

  @InjectMocks
  private TicketService ticketService;

  @Test
  void testBookTicket() {
    val ticket = Ticket.builder().build();
    when(ticketDao.book(ticket)).thenReturn(ticket);

    Ticket result = ticketService.bookTicket(ticket);

    assertEquals(ticket, result);
    verify(ticketDao).book(ticket);
  }

  @Test
  void testGetBookedTicketsByUser() {
    long userId = 1L;
    int pageSize = 10;
    int pageNum = 1;
    val tickets = Arrays.asList(Ticket.builder().build(), Ticket.builder().build());

    when(ticketDao.getBookedTicketsByUserId(userId, pageSize, pageNum)).thenReturn(tickets);

    val result = ticketService.getBookedTicketsByUser(userId, pageSize, pageNum);

    assertEquals(tickets, result);
    verify(ticketDao).getBookedTicketsByUserId(userId, pageSize, pageNum);
  }

  @Test
  void testGetBookedTicketsByEvent() {

    long eventId = 1L;
    int pageSize = 10;
    int pageNum = 1;
    val tickets = Arrays.asList(Ticket.builder().build(), Ticket.builder().build());

    when(ticketDao.getBookedTicketsByEventId(eventId, pageSize, pageNum)).thenReturn(tickets);

    val result = ticketService.getBookedTicketsByEvent(eventId, pageSize, pageNum);

    assertEquals(tickets, result);
    verify(ticketDao).getBookedTicketsByEventId(eventId, pageSize, pageNum);
  }

  @Test
  void testCancelTicket() {
    long ticketId = 1L;

    when(ticketDao.deleteById(ticketId)).thenReturn(true);

    val result = ticketService.cancelTicket(ticketId);

    assertTrue(result);
    verify(ticketDao).deleteById(ticketId);
  }
}
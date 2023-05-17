package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.entity.Event;
import com.example.entity.User;
import com.example.repository.TicketRepository;
import com.example.entity.Ticket;
import java.util.Arrays;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

  @Mock
  private TicketRepository ticketRepository;

  @InjectMocks
  private TicketService ticketService;

  @Test
  void testBookTicket() {
    val ticket = Ticket.builder().build();
    when(ticketRepository.save(ticket)).thenReturn(ticket);

    Ticket result = ticketService.bookTicket(ticket);

    assertEquals(ticket, result);
    verify(ticketRepository).save(ticket);
  }

  @Test
  void testGetBookedTicketsByUser() {
    int pageSize = 10;
    int pageNum = 1;
    val tickets = Arrays.asList(Ticket.builder().build(), Ticket.builder().build());
    val ticketsPage = new PageImpl<>(tickets);
    val user = User.builder().id(1L).build();

    when(ticketRepository.findAllByUserIdOrderByEventDateDesc(anyLong(), any(Pageable.class)))
        .thenReturn(ticketsPage);

    val result = ticketService.getTicketsByUserSortedByEventDateDesc(user, pageSize, pageNum);

    assertEquals(tickets, result);
    verify(ticketRepository).findAllByUserIdOrderByEventDateDesc(anyLong(), any(Pageable.class));
  }

  @Test
  void testGetBookedTicketsByEvent() {
    int pageSize = 10;
    int pageNum = 1;
    val tickets = Arrays.asList(Ticket.builder().build(), Ticket.builder().build());
    val ticketsPage = new PageImpl<>(tickets);
    long eventId = 23L;

    val event = Event.builder().id(eventId).build();

    when(ticketRepository.findAllByEventIdOrderByUserEmailAsc(eventId, PageRequest.of(pageNum, pageSize)))
        .thenReturn(ticketsPage);

    val result = ticketService.getTicketsByEventSortedByEventDateDesc(event, pageSize, pageNum);

    assertEquals(tickets, result);
  }

  @Test
  void testCancelTicket() {
    long ticketId = 1L;

    ticketService.cancelTicket(ticketId);

    verify(ticketRepository).deleteById(ticketId);
  }
}
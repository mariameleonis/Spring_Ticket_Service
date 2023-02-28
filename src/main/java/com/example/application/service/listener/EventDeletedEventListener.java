package com.example.application.service.listener;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import com.example.application.service.event.EventDeletedEvent;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventDeletedEventListener {

  private final TicketDao ticketDao;

  @EventListener
  public void handleEventDeletedEvent(EventDeletedEvent event) {
    long eventId = event.getEventId();

    val eventTicketsIds = ticketDao.getBookedTicketsByEventId(eventId)
        .stream()
        .map(Ticket::getId)
        .toList();

    ticketDao.deleteAllByTicketId(eventTicketsIds);
  }

}

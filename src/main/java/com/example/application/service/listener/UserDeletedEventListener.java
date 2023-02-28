package com.example.application.service.listener;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import com.example.application.service.event.UserDeletedEvent;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDeletedEventListener {

  private final TicketDao ticketDao;

  @EventListener
  public void handleUserDeletedEvent(UserDeletedEvent event) {
    long userId = event.getUserId();

    val userTicketsIds = ticketDao.getBookedTicketsByUserId(userId).stream()
        .map(Ticket::getId)
        .toList();

    ticketDao.deleteAllByTicketId(userTicketsIds);
  }
}

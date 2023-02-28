package com.example.application.service;

import com.example.application.dao.TicketDao;
import com.example.application.model.Ticket;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketService {

  @Autowired
  private TicketDao ticketDao;

  public Ticket bookTicket(Ticket ticket) {
    log.info("Booking ticket: {}", ticket);
    return ticketDao.book(ticket);

  }

  public List<Ticket> getBookedTicketsByUser(long userId, int pageSize, int pageNum) {
    log.info("Getting booked tickets for user with id {} (pageSize={}, pageNum={})", userId, pageSize, pageNum);
    return ticketDao.getBookedTicketsByUserId(userId, pageSize, pageNum);
  }

  public List<Ticket> getBookedTicketsByEvent(long eventId, int pageSize, int pageNum) {
    log.info("Getting booked tickets for event with id {} (pageSize={}, pageNum={})", eventId, pageSize, pageNum);
    return ticketDao.getBookedTicketsByEventId(eventId, pageSize, pageNum);

  }

  public boolean cancelTicket(long ticketId) {
    log.info("Canceling ticket with id {}", ticketId);
    val canceled = ticketDao.deleteById(ticketId);
    if (canceled) {
      log.info("Canceled ticket with id {}", ticketId);
    } else {
      log.info("Could not cancel ticket with id {}", ticketId);
    }
    return canceled;
  }

}
